package com.wcc.platform.repository.googledrive;

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
import com.wcc.platform.properties.FolderStorageProperties;
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
@Import({
  TestGoogleDriveConfig.class,
  com.wcc.platform.config.TestGoogleDriveRepositoryConfig.class
})
@DisplayName("GoogleDriveService Integration Tests")
class GoogleDriveFileStorageRepositoryIntegrationTest extends DefaultDatabaseSetup {

  private static final String TEST_FOLDER_ID = "test-folder-id";
  private static final String TEST_FILE_ID = "test-file-id";
  private static final String WEB_VIEW_LINK =
      "https://drive.google.com/file/d/" + TEST_FILE_ID + "/view";
  private static final String TEST_FILE_NAME = "test-file.pdf";
  private static final String TEST_CONTENT_TYPE = "application/pdf";
  private static final byte[] TEST_FILE_DATA = "test file content".getBytes();

  @Autowired private GoogleDriveFileStorageRepository googleDriveRepository;
  @Autowired private Drive mockDriveService;
  @Autowired private FolderStorageProperties properties;

  @Mock private Drive.Files mockFiles;
  @Mock private Drive.Files.Create mockCreate;
  @Mock private Drive.Files.Delete mockDelete;
  @Mock private Drive.Files.Get mockGet;
  @Mock private Drive.Files.List mockList;
  @Mock private Drive.Permissions mockPermissions;
  @Mock private Drive.Permissions.Create mockPermissionCreate;

  @BeforeEach
  void setUp() {
    org.mockito.MockitoAnnotations.openMocks(this);
    when(mockDriveService.files()).thenReturn(mockFiles);
    when(mockDriveService.permissions()).thenReturn(mockPermissions);
  }

  private File createTestFile() {
    return GoogleDriveTestUtils.createMockFile(TEST_FILE_ID, TEST_FILE_NAME, WEB_VIEW_LINK);
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
      var result =
          googleDriveRepository.uploadFile(
              TEST_FILE_NAME, TEST_CONTENT_TYPE, TEST_FILE_DATA, TEST_FOLDER_ID);

      // Then
      assertThat(result).isNotNull();
      assertThat(result.id()).isEqualTo(TEST_FILE_ID);
      assertThat(result.webLink()).isEqualTo(WEB_VIEW_LINK);
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
      var result = googleDriveRepository.uploadFile(multipartFile, TEST_FOLDER_ID);

      // Then
      assertThat(result).isNotNull();
      assertThat(result.id()).isEqualTo(TEST_FILE_ID);
      assertThat(result.webLink()).isEqualTo(WEB_VIEW_LINK);
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
                  googleDriveRepository.uploadFile(
                      TEST_FILE_NAME, TEST_CONTENT_TYPE, TEST_FILE_DATA, TEST_FOLDER_ID))
          .isInstanceOf(PlatformInternalException.class)
          .hasMessageContaining(
              "Failure to upload resources to google drive in respective folder id.");
    }

    @Test
    @DisplayName("Should throw PlatformInternalException when MultipartFile read fails")
    void shouldThrowExceptionWhenMultipartFileReadFails() {
      // Given
      MockMultipartFile multipartFile =
          new MockMultipartFile("file", TEST_FILE_NAME, TEST_CONTENT_TYPE, TEST_FILE_DATA) {
            @Override
            public byte[] getBytes() throws IOException {
              throw new IOException("Failed to read file");
            }
          };

      // When & Then
      assertThatThrownBy(() -> googleDriveRepository.uploadFile(multipartFile, TEST_FOLDER_ID))
          .isInstanceOf(PlatformInternalException.class)
          .hasMessageContaining(
              "Failure to upload resources to google drive in respective folder id.");
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

      // When
      googleDriveRepository.deleteFile(TEST_FILE_ID);

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
      assertThatThrownBy(() -> googleDriveRepository.deleteFile(TEST_FILE_ID))
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
      File result = googleDriveRepository.getFile(TEST_FILE_ID);

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
      assertThatThrownBy(() -> googleDriveRepository.getFile(TEST_FILE_ID))
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
      FileList result = googleDriveRepository.listFiles(pageSize);

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
      assertThatThrownBy(() -> googleDriveRepository.listFiles(pageSize))
          .isInstanceOf(PlatformInternalException.class)
          .hasMessageContaining("Failed to list files from Google Drive");
    }
  }
}
