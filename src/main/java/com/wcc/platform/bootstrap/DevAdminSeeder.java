package com.wcc.platform.bootstrap;

import com.wcc.platform.domain.auth.UserAccount;
import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.UserAccountRepository;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * Seeds the bootstrap user accounts at application startup from the {@code app.seed.users}
 * configuration list (by default only the platform admin). Can be disabled with {@code
 * app.seed.enabled=false}.
 *
 * <p>This is the only account seeding done by the application itself. QA and demo data — the
 * role accounts, mentors and mentorship cycles used by the local Docker stack — are created
 * through the public API by {@code scripts/init-local-env.sh}.
 */
@Component
public class DevAdminSeeder implements ApplicationRunner {

  private static final Logger LOG = LoggerFactory.getLogger(DevAdminSeeder.class);

  private final UserAccountRepository userAccountRepository;
  private final MemberRepository memberRepository;
  private final DevAdminSeederProperties seedProperties;
  private final SeedUsersProperties seedUsersProperties;
  private final PasswordEncoder passwordEncoder;

  /**
   * Constructs a DevAdminSeeder with repositories and seed configuration.
   *
   * @param userAccountRepository repository for user accounts
   * @param memberRepository repository for members, used to link each account to a member
   * @param seedProperties configuration for the seeder enabled flag
   * @param seedUsersProperties configuration for the list of users to seed
   * @param passwordEncoder Argon2 password encoder
   */
  public DevAdminSeeder(
      final UserAccountRepository userAccountRepository,
      final MemberRepository memberRepository,
      final DevAdminSeederProperties seedProperties,
      final SeedUsersProperties seedUsersProperties,
      final PasswordEncoder passwordEncoder) {
    this.userAccountRepository = userAccountRepository;
    this.memberRepository = memberRepository;
    this.seedProperties = seedProperties;
    this.seedUsersProperties = seedUsersProperties;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void run(final ApplicationArguments args) {
    seedUsers();
  }

  private void seedUsers() {
    if (!seedProperties.isEnabled()) {
      LOG.info("Admin seeder disabled (app.seed.enabled=false)");
      return;
    }
    seedConfiguredUsers();
  }

  private void seedConfiguredUsers() {
    for (final SeedUser user : seedUsersProperties.getUsers()) {
      if (!user.isEnabled()) {
        LOG.info("Seed user disabled, skipping: {}", user.getEmail());
        continue;
      }
      final Long memberId = resolveMemberId(user);
      provisionSeededAccount(user, memberId);
    }
  }

  /**
   * Ensures the configured account exists with the expected password and roles. When the account
   * already exists, its password and roles are reset to the configured values so the credentials
   * stay predictable across restarts.
   *
   * @param user the seed user configuration
   * @param memberId the member to link when creating a new account
   */
  private void provisionSeededAccount(final SeedUser user, final Long memberId) {
    final var email = user.getEmail();
    final var password = user.getPassword();
    final var roles = user.getRoles();
    if (!StringUtils.hasText(email) || !StringUtils.hasText(password)) {
      LOG.warn("Seed user skipped: email or password not provided");
      return;
    }
    if (CollectionUtils.isEmpty(roles)) {
      LOG.warn("Seed user skipped, no roles provided: {}", email);
      return;
    }

    final var hash = passwordEncoder.encode(password);
    final var existing = userAccountRepository.findByEmail(email);
    if (existing.isPresent()) {
      final var accountId = existing.get().getId();
      userAccountRepository.updatePassword(accountId, hash);
      userAccountRepository.updateRole(accountId, roles);
      LOG.info("Reset seeded user credentials: {} (roles: {})", email, roles);
      return;
    }

    userAccountRepository.create(new UserAccount(null, memberId, email, hash, roles, true));
    LOG.info("Seeded user: {} (roles: {})", email, roles);
  }

  /**
   * Resolves the member to link to a seeded user, creating a minimal one if it does not already
   * exist so that login returns full profile information.
   *
   * @param user the seed user configuration
   * @return the linked member id, or null when the email is missing
   */
  private Long resolveMemberId(final SeedUser user) {
    final var email = user.getEmail();
    if (!StringUtils.hasText(email)) {
      return null;
    }

    final var existing = memberRepository.findByEmail(email);
    if (existing.isPresent()) {
      return existing.get().getId();
    }

    final var member =
        Member.builder()
            .fullName(displayName(user))
            .position("Seed User " + displayName(user))
            .email(email)
            .slackDisplayName(slackName(email))
            .country(new Country("GB", "United Kingdom"))
            .memberTypes(user.getMemberTypes() == null ? List.of() : user.getMemberTypes())
            .build();
    return memberRepository.create(member).getId();
  }

  private String displayName(final SeedUser user) {
    return StringUtils.hasText(user.getFullName()) ? user.getFullName() : user.getEmail();
  }

  private String slackName(final String email) {
    return email.split("@", 2)[0].toLowerCase(Locale.ENGLISH);
  }
}
