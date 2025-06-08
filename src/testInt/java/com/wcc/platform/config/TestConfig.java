package com.wcc.platform.config;

import com.google.api.services.drive.Drive;
import com.wcc.platform.controller.ResourceController;
import com.wcc.platform.service.ResourceService;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Profile;

/**
 * Configuration class for integration tests.
 * Provides mock beans required for testing and excludes problematic beans.
 */
@Configuration
@Profile("test")
@EnableAutoConfiguration
@ComponentScan(
    basePackages = "com.wcc.platform",
    excludeFilters = {
      @ComponentScan.Filter(
          type = FilterType.ASSIGNABLE_TYPE,
          classes = {ResourceController.class, ResourceService.class})
    })
public class TestConfig {

  /**
   * Provides a mock Drive bean for testing.
   * This is required by GoogleDriveService but will not be used
   * since we're excluding the beans that depend on it.
   */
  @Bean
  @ConditionalOnMissingBean
  public Drive driveService() {
    return Mockito.mock(Drive.class);
  }
}
