package com.wcc.platform.repository.googledrive;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.wcc.platform.config.TestGoogleDriveConfig;
import com.wcc.platform.properties.FolderStorageProperties;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import({
  TestGoogleDriveConfig.class,
  com.wcc.platform.config.TestGoogleDriveRepositoryConfig.class
})
@DisplayName("GoogleDriveService Performance Tests")
class GoogleDriveFileStorageRepositoryPerformanceTest {

  @Autowired private GoogleDriveFileStorageRepository googleDriveRepository;

  @Autowired private Drive mockDriveService;

  @Autowired private FolderStorageProperties properties;

  @Mock private Drive.Files mockFiles;

  @Mock private Drive.Files.Create mockCreate;

  @Mock private Drive.Permissions mockPermissions;

  @Mock private Drive.Permissions.Create mockPermissionCreate;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    when(mockDriveService.files()).thenReturn(mockFiles);
    when(mockDriveService.permissions()).thenReturn(mockPermissions);
  }

  @Test
  @DisplayName("Should upload file within acceptable time limit")
  void shouldUploadFileWithinTimeLimit() throws IOException {
    // Given
    String fileName = "large-test-file.pdf";
    File mockFile = new File();
    mockFile.setId("test-file-id");
    mockFile.setName(fileName);

    when(mockFiles.create(any(File.class), any())).thenReturn(mockCreate);
    when(mockCreate.setFields(any(String.class))).thenReturn(mockCreate);
    when(mockCreate.execute()).thenReturn(mockFile);
    when(mockPermissions.create(any(String.class), any(Permission.class)))
        .thenReturn(mockPermissionCreate);
    when(mockPermissionCreate.execute()).thenReturn(new Permission());

    // When
    Instant start = Instant.now();
    byte[] largeFileData = new byte[1024 * 1024]; // 1MB file
    var result =
        googleDriveRepository.uploadFile(fileName, "application/pdf", largeFileData, "folder-id");
    Instant end = Instant.now();
    Duration duration = Duration.between(start, end);

    // Then
    assertThat(result).isNotNull();
    assertThat(duration).isLessThan(Duration.ofSeconds(5)); // Should complete within 5 seconds
  }
}
