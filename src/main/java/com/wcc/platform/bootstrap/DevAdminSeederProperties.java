package com.wcc.platform.bootstrap;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for the development user account seeder. Binds to the {@code app.seed}
 * prefix and controls whether seeding is active at application startup.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.seed")
public class DevAdminSeederProperties {
  private boolean enabled;
}