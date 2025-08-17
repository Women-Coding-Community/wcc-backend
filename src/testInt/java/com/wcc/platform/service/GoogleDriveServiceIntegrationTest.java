package com.wcc.platform.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import com.wcc.platform.config.TestGoogleDriveConfig;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.repository.googledrive.GoogleDriveService;
import com.wcc.platform.repository.postgres.DefaultDatabaseSetup;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestGoogleDriveConfig.class)
@DisplayName("GoogleDriveService Integration Tests")
class GoogleDriveServiceIntegrationTest extends DefaultDatabaseSetup {

  private static final String TEST_FOLDER_ID = "test-folder-id";
  private static final String TEST_FILE_ID = "test-file-id";
  private static final String TEST_FILE_NAME = "test-file.pdf";
  private static final String TEST_CONTENT_TYPE = "application/pdf";
  private static final byte[] TEST_FILE_DATA = "test file content".getBytes();

  @Autowired private GoogleDriveService googleDriveService;
  @Autowired private Drive mockDriveService;

  @Mock private Drive.Files mockFiles;
  @Mock private Drive.Files.Create mockCreate;
  @Mock private Drive.Files.Delete mockDelete;
  @Mock private Drive.Files.Get mockGet;
  @Mock private Drive.Files.List mockList;
  @Mock private Drive.Permissions mockPermissions;
  @Mock private Drive.Permissions.Create mockPermissionCreate;

  @BeforeEach
  void setUp() throws IOException {
    // Setup common mock behavior
    when(mockDriveService.files()).thenReturn(mockFiles);
    when(mockDriveService.permissions()).thenReturn(mockPermissions);
  }

  private File createTestFile() {
    File file = new File();
    file.setId(TEST_FILE_ID);
    file.setName(TEST_FILE_NAME);
    file.setWebViewLink("https://drive.google.com/file/d/" + TEST_FILE_ID + "/view");
    return file;
  }

  private FileList createTestFileList() {
    FileList fileList = new FileList();
    fileList.setFiles(List.of(createTestFile()));
    fileList.setNextPageToken("next-page-token");
    return fileList;
  }

  @Nested
  @DisplayName("File Upload Tests")
  class FileUploadTests {

    @Test
    @DisplayName("Should successfully upload file with byte array")
    void shouldUploadFileSuccessfully() throws IOException {
      // Given
      File expectedFile = createTestFile();
      when(mockFiles.create(any(File.class), any())).thenReturn(mockCreate);
      when(mockCreate.setFields(any(String.class))).thenReturn(mockCreate);
      when(mockCreate.execute()).thenReturn(expectedFile);
      when(mockPermissions.create(eq(TEST_FILE_ID), any(Permission.class)))
          .thenReturn(mockPermissionCreate);
      when(mockPermissionCreate.execute()).thenReturn(new Permission());

      // When
      File result =
          googleDriveService.uploadFile(TEST_FILE_NAME, TEST_CONTENT_TYPE, TEST_FILE_DATA);

      // Then
      assertThat(result).isNotNull();
      assertThat(result.getId()).isEqualTo(TEST_FILE_ID);
      assertThat(result.getName()).isEqualTo(TEST_FILE_NAME);
      verify(mockCreate).execute();
      verify(mockPermissionCreate).execute();
    }

    @Test
    @DisplayName("Should successfully upload MultipartFile")
    void shouldUploadMultipartFileSuccessfully() throws IOException {
      // Given
      MockMultipartFile multipartFile =
          new MockMultipartFile("file", TEST_FILE_NAME, TEST_CONTENT_TYPE, TEST_FILE_DATA);
      File expectedFile = createTestFile();

      when(mockFiles.create(any(File.class), any())).thenReturn(mockCreate);
      when(mockCreate.setFields(any(String.class))).thenReturn(mockCreate);
      when(mockCreate.execute()).thenReturn(expectedFile);
      when(mockPermissions.create(eq(TEST_FILE_ID), any(Permission.class)))
          .thenReturn(mockPermissionCreate);
      when(mockPermissionCreate.execute()).thenReturn(new Permission());

      // When
      File result = googleDriveService.uploadFile(multipartFile);

      // Then
      assertThat(result).isNotNull();
      assertThat(result.getId()).isEqualTo(TEST_FILE_ID);
      assertThat(result.getName()).isEqualTo(TEST_FILE_NAME);
    }

    @Test
    @DisplayName("Should throw PlatformInternalException when upload fails")
    void shouldThrowExceptionWhenUploadFails() throws IOException {
      // Given
      when(mockFiles.create(any(File.class), any())).thenReturn(mockCreate);
      when(mockCreate.setFields(any(String.class))).thenReturn(mockCreate);
      when(mockCreate.execute()).thenThrow(new IOException("Upload failed"));

      // When & Then
      assertThatThrownBy(
              () ->
                  googleDriveService.uploadFile(TEST_FILE_NAME, TEST_CONTENT_TYPE, TEST_FILE_DATA))
          .isInstanceOf(PlatformInternalException.class)
          .hasMessageContaining("Failed to upload file to Google Drive");
    }

    @Test
    @DisplayName("Should throw PlatformInternalException when MultipartFile read fails")
    void shouldThrowExceptionWhenMultipartFileReadFails() throws IOException {
      // Given
      MockMultipartFile multipartFile =
          new MockMultipartFile("file", TEST_FILE_NAME, TEST_CONTENT_TYPE, TEST_FILE_DATA) {
            @Override
            public byte[] getBytes() throws IOException {
              throw new IOException("Failed to read file");
            }
          };

      // When & Then
      assertThatThrownBy(() -> googleDriveService.uploadFile(multipartFile))
          .isInstanceOf(PlatformInternalException.class)
          .hasMessageContaining("Failed to read file data");
    }
  }

  @Nested
  @DisplayName("File Deletion Tests")
  class FileDeletionTests {

    @Test
    @DisplayName("Should successfully delete file")
    void shouldDeleteFileSuccessfully() throws IOException {
      // Given
      when(mockFiles.delete(TEST_FILE_ID)).thenReturn(mockDelete);
      when(mockDelete.execute()).thenReturn(null);

      // When
      googleDriveService.deleteFile(TEST_FILE_ID);

      // Then
      verify(mockFiles).delete(TEST_FILE_ID);
      verify(mockDelete).execute();
    }

    @Test
    @DisplayName("Should throw PlatformInternalException when deletion fails")
    void shouldThrowExceptionWhenDeletionFails() throws IOException {
      // Given
      when(mockFiles.delete(TEST_FILE_ID)).thenReturn(mockDelete);
      doThrow(new IOException("Deletion failed")).when(mockDelete).execute();

      // When & Then
      assertThatThrownBy(() -> googleDriveService.deleteFile(TEST_FILE_ID))
          .isInstanceOf(PlatformInternalException.class)
          .hasMessageContaining("Failed to delete file from Google Drive");
    }
  }

  @Nested
  @DisplayName("File Retrieval Tests")
  class FileRetrievalTests {

    @Test
    @DisplayName("Should successfully get file")
    void shouldGetFileSuccessfully() throws IOException {
      // Given
      File expectedFile = createTestFile();
      when(mockFiles.get(TEST_FILE_ID)).thenReturn(mockGet);
      when(mockGet.setFields(any(String.class))).thenReturn(mockGet);
      when(mockGet.execute()).thenReturn(expectedFile);

      // When
      File result = googleDriveService.getFile(TEST_FILE_ID);

      // Then
      assertThat(result).isNotNull();
      assertThat(result.getId()).isEqualTo(TEST_FILE_ID);
      assertThat(result.getName()).isEqualTo(TEST_FILE_NAME);
      verify(mockGet).setFields("id, name, webViewLink");
    }

    @Test
    @DisplayName("Should throw PlatformInternalException when get file fails")
    void shouldThrowExceptionWhenGetFileFails() throws IOException {
      // Given
      when(mockFiles.get(TEST_FILE_ID)).thenReturn(mockGet);
      when(mockGet.setFields(any(String.class))).thenReturn(mockGet);
      when(mockGet.execute()).thenThrow(new IOException("Get file failed"));

      // When & Then
      assertThatThrownBy(() -> googleDriveService.getFile(TEST_FILE_ID))
          .isInstanceOf(PlatformInternalException.class)
          .hasMessageContaining("Failed to get file from Google Drive");
    }
  }

  @Nested
  @DisplayName("File Listing Tests")
  class FileListingTests {

    @Test
    @DisplayName("Should successfully list files")
    void shouldListFilesSuccessfully() throws IOException {
      // Given
      int pageSize = 10;
      FileList expectedFileList = createTestFileList();
      when(mockFiles.list()).thenReturn(mockList);
      when(mockList.setPageSize(pageSize)).thenReturn(mockList);
      when(mockList.setFields(any(String.class))).thenReturn(mockList);
      when(mockList.execute()).thenReturn(expectedFileList);

      // When
      FileList result = googleDriveService.listFiles(pageSize);

      // Then
      assertThat(result).isNotNull();
      assertThat(result.getFiles()).hasSize(1);
      verify(mockList).setPageSize(pageSize);
      verify(mockList).setFields("nextPageToken, files(id, name, webViewLink)");
    }

    @Test
    @DisplayName("Should throw PlatformInternalException when list files fails")
    void shouldThrowExceptionWhenListFilesFails() throws IOException {
      // Given
      int pageSize = 10;
      when(mockFiles.list()).thenReturn(mockList);
      when(mockList.setPageSize(pageSize)).thenReturn(mockList);
      when(mockList.setFields(any(String.class))).thenReturn(mockList);
      when(mockList.execute()).thenThrow(new IOException("List files failed"));

      // When & Then
      assertThatThrownBy(() -> googleDriveService.listFiles(pageSize))
          .isInstanceOf(PlatformInternalException.class)
          .hasMessageContaining("Failed to list files from Google Drive");
    }
  }
}
