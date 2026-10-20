package com.wcc.platform.service.mentorship;

import static com.wcc.platform.factories.SetupMentorFactories.createMentorTest;
import static org.assertj.core.api.Assertions.assertThat;

import com.wcc.platform.domain.auth.UserAccount;
import com.wcc.platform.domain.platform.type.RoleType;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.UserAccountRepository;
import com.wcc.platform.domain.auth.UserToken;
import com.wcc.platform.repository.postgres.DefaultDatabaseSetup;
import com.wcc.platform.service.AuthService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AuthServiceIntegrationTest extends DefaultDatabaseSetup {

  private static final String PASSWORD = "pwd";

  @Autowired private AuthService service;
  @Autowired private MemberRepository repository;
  @Autowired private UserAccountRepository userAccountRepository;
  @Autowired private PasswordEncoder passwordEncoder;

  private UserAccount userAccount;

  @BeforeEach
  void setUp() {
    var mentor = createMentorTest(4L, "mentor postgres", "user@account.com");
    var mentorOptional = repository.findByEmail(mentor.getEmail());
    mentorOptional.ifPresent(value -> repository.deleteById(value.getId()));

    var savedMember = repository.create(mentor);

    var users = userAccountRepository.findAll();
    users.forEach(user -> userAccountRepository.deleteById(user.getId()));

    userAccountRepository.create(
        new UserAccount(
            1,
            savedMember.getId(),
            mentor.getEmail(),
            passwordEncoder.encode(PASSWORD),
            List.of(RoleType.ADMIN),
            true));

    userAccount = userAccountRepository.findByEmail(mentor.getEmail()).orElseThrow();
  }

  @Test
  void testGetMemberNull() {
    var response = service.getMember(null);

    assertThat(response).isNull();
  }

  @Test
  void testGetMemberNotNull() {
    var response = service.getMember(userAccount.getMemberId());

    assertThat(response).isNotNull();
    assertThat(response.getId()).isEqualTo(userAccount.getMemberId());
  }

  @Test
  void testEmailNotFoundAuthenticateAndIssueToken() {
    var response = service.authenticateAndIssueToken("invalid_personl@google.com", "pwd");

    assertThat(response.isPresent()).isFalse();
  }

  @Test
  void testNotFoundToken() {
    var response = service.authenticateByToken("invalidToken");

    assertThat(response.isPresent()).isFalse();
  }

  @Test
  void shouldOnlyKeepOneActiveTokenPerUserAfterSecondLogin() {
    var firstToken = service.authenticateAndIssueToken(userAccount.getEmail(), PASSWORD).orElseThrow();

    var secondToken = service.authenticateAndIssueToken(userAccount.getEmail(), PASSWORD).orElseThrow();

    assertThat(service.authenticateByToken(firstToken.getToken())).isEmpty();
    assertThat(service.authenticateByToken(secondToken.getToken())).isPresent();
  }

  /**
   * Fires several logins for the same account at the same instant (a {@link CyclicBarrier} holds
   * every thread until all have started, then releases them together) to prove the per-user
   * advisory lock in {@code generateUserToken} actually serializes concurrent logins, rather than
   * relying on sequential calls to demonstrate the single-active-token invariant.
   */
  @Test
  void shouldKeepExactlyOneActiveTokenUnderConcurrentLogins() throws Exception {
    final int concurrency = 5;
    final ExecutorService executor = Executors.newFixedThreadPool(concurrency);
    final CyclicBarrier barrier = new CyclicBarrier(concurrency);
    final List<Future<UserToken>> futures = new ArrayList<>();

    try {
      for (int i = 0; i < concurrency; i++) {
        futures.add(
            executor.submit(
                () -> {
                  barrier.await();
                  return service
                      .authenticateAndIssueToken(userAccount.getEmail(), PASSWORD)
                      .orElseThrow();
                }));
      }

      final List<UserToken> issuedTokens = new ArrayList<>();
      for (final Future<UserToken> future : futures) {
        issuedTokens.add(future.get(60, TimeUnit.SECONDS));
      }

      assertThat(issuedTokens).hasSize(concurrency);

      final long activeCount =
          issuedTokens.stream()
              .filter(token -> service.authenticateByToken(token.getToken()).isPresent())
              .count();

      assertThat(activeCount).isEqualTo(1);
    } finally {
      executor.shutdownNow();
    }
  }
}
