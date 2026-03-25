package com.wcc.platform.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for the password reset flow.
 *
 * <p>Bound to the {@code app.reset-password} prefix in the application configuration.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.reset-password")
public class PasswordResetConfig {

  /** Base URL of the frontend application. Used to construct the reset link sent by email. */
  private String baseUrl = "http://localhost:3000";

  /** Time-to-live for a password reset token, in minutes. Defaults to 60. */
  private int ttlMinutes = 60;
}
