package com.wcc.platform.config;

import com.google.api.services.drive.Drive;
import com.wcc.platform.properties.FolderStorageProperties;
import com.wcc.platform.repository.googledrive.GoogleDriveFileStorageRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Test configuration that provides a primary GoogleDriveRepository bean built with the mocked Drive
 * service to prevent real HTTP calls during integration tests.
 */
@TestConfiguration
@Profile("test")
public class TestGoogleDriveRepositoryConfig {

  @Bean
  @Primary
  public GoogleDriveFileStorageRepository googleDriveRepository(
      final Drive drive, final FolderStorageProperties folders) {
    return new GoogleDriveFileStorageRepository(drive, folders);
  }
}
