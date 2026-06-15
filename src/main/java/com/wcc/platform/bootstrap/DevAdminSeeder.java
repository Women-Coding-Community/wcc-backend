package com.wcc.platform.bootstrap;

import static com.wcc.platform.domain.cms.attributes.CodeLanguage.JAVASCRIPT;
import static com.wcc.platform.domain.cms.attributes.ProficiencyLevel.BEGINNER;
import static com.wcc.platform.domain.cms.attributes.ProficiencyLevel.EXPERT;
import static com.wcc.platform.domain.cms.attributes.TechnicalArea.FULLSTACK;

import com.wcc.platform.domain.auth.UserAccount;
import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.MentorshipFocusArea;
import com.wcc.platform.domain.cms.pages.mentorship.MenteeSection;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.platform.mentorship.LanguageProficiency;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.domain.platform.mentorship.Skills;
import com.wcc.platform.domain.platform.mentorship.TechnicalAreaProficiency;
import com.wcc.platform.domain.platform.type.RoleType;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.MentorRepository;
import com.wcc.platform.repository.UserAccountRepository;
import com.wcc.platform.service.MentorshipService;
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
 * Seeds user accounts at application startup from the {@code app.seed.users} configuration list.
 * Can be disabled with {@code app.seed.enabled=false}. Users holding the MENTOR role are
 * provisioned with an active mentor profile so they appear in the admin portal's mentor list.
 */
@Component
public class DevAdminSeeder implements ApplicationRunner {

  private static final Logger LOG = LoggerFactory.getLogger(DevAdminSeeder.class);
  private static final int YEARS_EXPERIENCE = 5;

  private final UserAccountRepository userAccountRepository;
  private final MemberRepository memberRepository;
  private final MentorRepository mentorRepository;
  private final MentorshipService mentorshipService;
  private final DevAdminSeederProperties seedProperties;
  private final SeedUsersProperties seedUsersProperties;
  private final PasswordEncoder passwordEncoder;

  /**
   * Constructs a DevAdminSeeder with repositories, mentorship service and seed configuration.
   *
   * @param userAccountRepository repository for user accounts
   * @param memberRepository repository for members
   * @param mentorRepository repository for mentors, used to activate seeded mentor profiles
   * @param mentorshipService service used to provision mentor profiles for MENTOR-role users
   * @param seedProperties configuration for the seeder enabled flag
   * @param seedUsersProperties configuration for the list of users to seed
   * @param passwordEncoder Argon2 password encoder
   */
  public DevAdminSeeder(
      final UserAccountRepository userAccountRepository,
      final MemberRepository memberRepository,
      final MentorRepository mentorRepository,
      final MentorshipService mentorshipService,
      final DevAdminSeederProperties seedProperties,
      final SeedUsersProperties seedUsersProperties,
      final PasswordEncoder passwordEncoder) {
    this.userAccountRepository = userAccountRepository;
    this.memberRepository = memberRepository;
    this.mentorRepository = mentorRepository;
    this.mentorshipService = mentorshipService;
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
   * was already auto-provisioned (for example by mentor creation, which assigns a random password),
   * its password and roles are reset to the configured values so the credentials are predictable.
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
   * Resolves the member to link to a seeded user, creating one if it does not already exist so that
   * login returns full profile information. Users holding the MENTOR role are provisioned with a
   * full mentor profile so they appear in the admin portal's mentor list.
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

    if (user.getRoles() != null && user.getRoles().contains(RoleType.MENTOR)) {
      final var mentorId = createMentorProfile(user).getId();
      mentorRepository.updateProfileStatus(mentorId, ProfileStatus.ACTIVE);
      LOG.info("Seeded ACTIVE mentor profile: {} (id: {})", email, mentorId);
      return mentorId;
    }

    final var member =
        Member.builder()
            .fullName(displayName(user))
            .position("QA Seed User " + user.getFullName())
            .email(email)
            .slackDisplayName(slackName(email))
            .country(new Country("GB", "United Kingdom"))
            .memberTypes(user.getMemberTypes() == null ? List.of() : user.getMemberTypes())
            .build();
    return memberRepository.create(member).getId();
  }

  /**
   * Creates a minimal but valid mentor profile for a seeded user. The service persists it in
   * PENDING status; the caller then activates it directly (without sending an approval email) so
   * the mentor appears as ACTIVE in the admin portal.
   *
   * @param user the seed user configuration
   * @return the created mentor, including its generated id
   */
  private Mentor createMentorProfile(final SeedUser user) {
    final var mentor =
        Mentor.mentorBuilder()
            .fullName(displayName(user))
            .position("QA Mentor")
            .email(user.getEmail())
            .slackDisplayName(slackName(user.getEmail()))
            .country(new Country("GB", "United Kingdom"))
            .profileStatus(ProfileStatus.PENDING)
            .skills(
                new Skills(
                    YEARS_EXPERIENCE,
                    List.of(new TechnicalAreaProficiency(FULLSTACK, EXPERT)),
                    List.of(new LanguageProficiency(JAVASCRIPT, BEGINNER)),
                    List.of(MentorshipFocusArea.GROW_BEGINNER_TO_MID)))
            .spokenLanguages(List.of("English"))
            .bio("QA seeded mentor profile for testing the admin portal.")
            .menteeSection(
                new MenteeSection("Motivated mentees keen to grow.", null, null, List.of()))
            .isWomen(true)
            .build();
    return mentorshipService.create(mentor);
  }

  private String displayName(final SeedUser user) {
    return StringUtils.hasText(user.getFullName()) ? user.getFullName() : user.getEmail();
  }

  private String slackName(final String email) {
    return email.split("@", 2)[0].toLowerCase(Locale.ENGLISH);
  }
}
