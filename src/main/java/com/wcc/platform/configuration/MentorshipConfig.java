package com.wcc.platform.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for mentorship-related settings. This class binds to properties under
 * the prefix "mentorship" in the application configuration file.
 *
 * <p>Properties: - validation.enabled: Flag to enable or disable mentorship cycle validation during
 * mentee registration. Default is true. Set to false for testing and debugging purposes.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "mentorship")
public class MentorshipConfig {

  /** Validation-specific configuration. */
  private Validation validation = new Validation();

  /** Nested configuration class for validation settings. */
  @Getter
  @Setter
  public static class Validation {
    /** Flag to enable/disable mentorship cycle validation. */
    private boolean enabled = true;
  }
}
