package com.wcc.platform.config;

import com.google.api.services.drive.Drive;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Test configuration for Google Drive integration. Provides a mock Drive service to prevent real
 * authentication during tests.
 */
@TestConfiguration
@Profile("test")
public class TestGoogleDriveConfig {

  /**
   * Creates a mock Drive service for testing. This prevents the GoogleDriveService from trying to
   * authenticate with real credentials.
   *
   * @return A mock Drive service
   */
  @Bean
  @Primary
  public Drive driveService() {
    return Mockito.mock(Drive.class);
  }
}
