package com.wcc.platform.bootstrap;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for additional user accounts seeded at application startup. Binds to the
 * {@code app.seed.users} list, allowing roles such as MENTORSHIP_ADMIN, MENTOR and LEADER to be
 * provisioned automatically alongside the default admin user.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.seed")
public class SeedUsersProperties {

  private List<SeedUser> users = new ArrayList<>();
}
