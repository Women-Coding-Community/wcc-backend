package com.wcc.platform.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for notification-related settings.
 *
 * <p>Binds to the {@code notification} prefix in application configuration.
 */
@Configuration
@ConfigurationProperties(prefix = "notification")
@Getter
@Setter
public class NotificationConfig {
  private String mentorProfileUrl;
  private String volunteerUrl;
  private String mentorshipEmail;
}
