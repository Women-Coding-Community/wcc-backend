package com.wcc.platform.bootstrap;

import com.wcc.platform.domain.auth.UserAccount;
import com.wcc.platform.domain.platform.type.RoleType;
import com.wcc.platform.repository.UserAccountRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Seeds a default admin user for local testing so the frontend can authenticate against real data.
 * Enabled by default, can be disabled with app.seed.admin.enabled=false.
 */
@Component
public class DevAdminSeeder implements ApplicationRunner {

  private static final Logger LOG = LoggerFactory.getLogger(DevAdminSeeder.class);

  private final UserAccountRepository userAccountRepository;
  private final DevAdminSeederProperties seedProperties;
  private final PasswordEncoder passwordEncoder;

  /**
   * Constructs a DevAdminSeeder instance with dependencies and configuration values.
   *
   * @param userAccountRepository repository for managing user accounts, used to check the presence
   *     of and create the default admin user.
   * @param seedProperties configuration properties for the dev admin seeder.
   * @param passwordEncoder Argon2 password encoder.
   */
  public DevAdminSeeder(
      final UserAccountRepository userAccountRepository,
      final DevAdminSeederProperties seedProperties,
      final PasswordEncoder passwordEncoder) {
    this.userAccountRepository = userAccountRepository;
    this.seedProperties = seedProperties;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void run(final ApplicationArguments args) {
    if (!seedProperties.isEnabled()) {
      LOG.info("Admin seeder disabled (app.seed.admin.enabled=false)");
      return;
    }
    final var email = seedProperties.getEmail();
    if (!StringUtils.hasText(email) || !StringUtils.hasText(seedProperties.getPassword())) {
      LOG.warn("Admin seeder skipped: email or password not provided");
      return;
    }

    final var existing = userAccountRepository.findByEmail(email);
    if (existing.isPresent()) {
      LOG.info("Default admin user already exists: {}", email);
      return;
    }

    final var hash = passwordEncoder.encode(seedProperties.getPassword());

    final UserAccount user =
        new UserAccount(1, 1L, seedProperties.getEmail(), hash, List.of(RoleType.ADMIN), true);
    userAccountRepository.create(user);
    LOG.info(
        "Seeded default admin user: {} (roles: {})", seedProperties.getEmail(), user.getRoles());
  }
}
