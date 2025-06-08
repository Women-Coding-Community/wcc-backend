package com.wcc.platform.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

/**
 * Integration tests for {@link GoogleDriveService}. These tests use a test implementation of
 * GoogleDriveService that stores files in memory.
 */
@ExtendWith(MockitoExtension.class)
class GoogleDriveServiceIntTest {

  private GoogleDriveService googleDriveService;

  private String testFileId;

  @Mock
  private Drive mockDrive;

  @BeforeEach
  void setUp() {
    // Initialize the test service with a mock Drive
    googleDriveService = new TestGoogleDriveService(mockDrive);

    // Clean up any test files that might have been left from previous test runs
    testFileId = null;
  }

  @AfterEach
  void tearDown() {
    // Clean up any test files created during the test
    if (testFileId != null) {
      try {
        googleDriveService.deleteFile(testFileId);
      } catch (Exception e) {
        // Ignore exceptions during cleanup
      }
    }
  }

  @Test
  void testUploadAndGetFile() {
    // Arrange
    String fileName = "test-file-" + UUID.randomUUID() + ".txt";
    String contentType = "text/plain";
    byte[] fileData = "Hello, World!".getBytes();

    // Act
    File uploadedFile = googleDriveService.uploadFile(fileName, contentType, fileData);
    testFileId = uploadedFile.getId(); // Store for cleanup

    File retrievedFile = googleDriveService.getFile(testFileId);

    // Assert
    assertNotNull(uploadedFile);
    assertNotNull(uploadedFile.getId());
    assertEquals(fileName, uploadedFile.getName());
    assertNotNull(uploadedFile.getWebViewLink());

    assertNotNull(retrievedFile);
    assertEquals(testFileId, retrievedFile.getId());
    assertEquals(fileName, retrievedFile.getName());
    assertEquals(uploadedFile.getWebViewLink(), retrievedFile.getWebViewLink());
  }

  @Test
  void testUploadMultipartFile() {
    // Arrange
    String fileName = "test-multipart-file-" + UUID.randomUUID() + ".txt";
    String contentType = "text/plain";
    byte[] fileData = "Hello, World from MultipartFile!".getBytes();

    MultipartFile multipartFile = new MockMultipartFile(fileName, fileName, contentType, fileData);

    // Act
    File uploadedFile = googleDriveService.uploadFile(multipartFile);
    testFileId = uploadedFile.getId(); // Store for cleanup

    // Assert
    assertNotNull(uploadedFile);
    assertNotNull(uploadedFile.getId());
    assertEquals(fileName, uploadedFile.getName());
    assertNotNull(uploadedFile.getWebViewLink());
  }

  @Test
  void testDeleteFile() {
    // Arrange
    String fileName = "test-file-to-delete-" + UUID.randomUUID() + ".txt";
    String contentType = "text/plain";
    byte[] fileData = "This file will be deleted.".getBytes();

    File uploadedFile = googleDriveService.uploadFile(fileName, contentType, fileData);
    String fileId = uploadedFile.getId();

    // Act
    googleDriveService.deleteFile(fileId);

    // Assert - Verify that getting the deleted file throws an exception
    PlatformInternalException exception =
        assertThrows(PlatformInternalException.class, () -> googleDriveService.getFile(fileId));

    assertTrue(exception.getMessage().contains("Failed to get file from Google Drive"));

    // Don't set testFileId since we've already deleted the file
  }

  @Test
  void testListFiles() {
    // Arrange
    String fileName = "test-file-for-listing-" + UUID.randomUUID() + ".txt";
    String contentType = "text/plain";
    byte[] fileData = "This file should appear in the list.".getBytes();

    File uploadedFile = googleDriveService.uploadFile(fileName, contentType, fileData);
    testFileId = uploadedFile.getId(); // Store for cleanup

    // Act
    FileList fileList = googleDriveService.listFiles(10);

    // Assert
    assertNotNull(fileList);
    assertNotNull(fileList.getFiles());

    // The file list should contain at least our uploaded file
    boolean foundUploadedFile =
        fileList.getFiles().stream().anyMatch(file -> file.getId().equals(testFileId));

    assertTrue(foundUploadedFile, "The uploaded file should be in the file list");
  }

  @Test
  void testGetNonExistentFile() {
    // Arrange
    String nonExistentFileId = "non-existent-file-id";

    // Act & Assert
    PlatformInternalException exception =
        assertThrows(
            PlatformInternalException.class, () -> googleDriveService.getFile(nonExistentFileId));

    assertTrue(exception.getMessage().contains("Failed to get file from Google Drive"));
  }
}
