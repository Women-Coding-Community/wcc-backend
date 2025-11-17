package com.wcc.platform.service.mentorship;

import static com.wcc.platform.factories.SetupMentorshipFactories.createMentorTest;
import static org.assertj.core.api.Assertions.assertThat;

import com.wcc.platform.domain.auth.UserAccount;
import com.wcc.platform.domain.platform.type.RoleType;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.UserAccountRepository;
import com.wcc.platform.repository.postgres.DefaultDatabaseSetup;
import com.wcc.platform.service.AuthService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AuthServiceIntegrationTest extends DefaultDatabaseSetup {

  @Autowired private AuthService service;
  @Autowired private MemberRepository repository;
  @Autowired private UserAccountRepository userAccountRepository;

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
        UserAccount.builder()
            .memberId(savedMember.getId())
            .email(mentor.getEmail())
            .roles(List.of(RoleType.ADMIN))
            .enabled(true)
            .passwordHash("newHash")
            .build());

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
}
