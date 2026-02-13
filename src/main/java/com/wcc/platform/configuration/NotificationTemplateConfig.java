package com.wcc.platform.configuration;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "notification.template")
@Getter
public class NotificationTemplateConfig {
  private String websiteLink;
  private String mentorProfilePath;
}
