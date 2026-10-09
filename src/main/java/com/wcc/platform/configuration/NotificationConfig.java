package com.wcc.platform.configuration;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for notification-related settings.
 *
 * <p>Binds to the {@code notification} prefix in application configuration.
 */
@Configuration
@ConfigurationProperties(prefix = "notification")
@Validated
@Getter
@Setter
public class NotificationConfig {
  @NotBlank private String mentorProfileUrl;
  @NotBlank private String volunteerUrl;
  @NotBlank private String mentorshipEmail;
}
