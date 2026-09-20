package com.wcc.platform.repository.googledrive;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import com.wcc.platform.configuration.GoogleDriveConfig;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.properties.FolderStorageProperties;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class GoogleDriveFileStorageRepositoryTest {

  private static final String FOLDER_ID_ROOT = "test-folder-id";
  private final Drive driveServiceMock = mock(Drive.class);
  private final Drive.Files filesMock = mock(Drive.Files.class);
  private final Drive.Files.Create fileCreateMock = mock(Drive.Files.Create.class);
  private final Drive.Permissions permissionsMock = mock(Drive.Permissions.class);
  private final Drive.Permissions.Create permissionCreateMock =
      mock(Drive.Permissions.Create.class);

  private FolderStorageProperties properties;
  private GoogleDriveFileStorageRepository service;

  @BeforeEach
  void setUp() {
    properties = new FolderStorageProperties();
    properties.setMainFolder(FOLDER_ID_ROOT);

    service = new GoogleDriveFileStorageRepository(driveServiceMock, properties);
  }

  @Test
  @DisplayName(
      "Given blank credentials JSON, when constructing repository, then throw IllegalStateException")
  void shouldThrowIllegalStateExceptionWhenCredentialsJsonIsBlank() {
    var config = new GoogleDriveConfig();
    config.setCredentialsJson("");

    assertThatThrownBy(
            () -> new GoogleDriveFileStorageRepository(new FolderStorageProperties(), config))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("GOOGLE_DRIVE_CREDENTIALS_JSON");
  }

  @Test
  @DisplayName(
      "Given valid file bytes and metadata, when uploading file, then return FileDetails and create public permission")
  void shouldUploadFileSuccessfully() throws Exception {
    File expectedFile = new File();
    expectedFile.setId("test-file-id");
    expectedFile.setName("test-file");
    expectedFile.setWebViewLink("http://google-drive-link");

    when(driveServiceMock.files()).thenReturn(filesMock);
    when(filesMock.create(any(File.class), any())).thenReturn(fileCreateMock);
    when(fileCreateMock.setSupportsAllDrives(true)).thenReturn(fileCreateMock);
    when(fileCreateMock.setFields("id, name, webViewLink")).thenReturn(fileCreateMock);
    when(fileCreateMock.execute()).thenReturn(expectedFile);

    when(driveServiceMock.permissions()).thenReturn(permissionsMock);
    when(permissionsMock.create(eq(expectedFile.getId()), any(Permission.class)))
        .thenReturn(permissionCreateMock);
    when(permissionCreateMock.setSupportsAllDrives(true)).thenReturn(permissionCreateMock);
    when(permissionCreateMock.execute()).thenReturn(new Permission());

    var actualFile =
        service.uploadFile("test-file", "text/plain", "Hello world".getBytes(), FOLDER_ID_ROOT);

    assertThat(actualFile).isNotNull();
    assertThat(actualFile.id()).isEqualTo(expectedFile.getId());
    assertThat(actualFile.webLink()).isEqualTo(expectedFile.getWebViewLink());

    verify(permissionsMock).create(eq(expectedFile.getId()), any(Permission.class));
    verify(fileCreateMock).execute();
  }

  @Test
  @DisplayName(
      "Given valid MultipartFile, when uploading file, then return FileDetails and create public permission")
  void shouldUploadMultipartFileSuccessfully() throws Exception {
    File expectedFile = new File();
    expectedFile.setId("test-file-id");
    expectedFile.setName("test-file");
    expectedFile.setWebViewLink("http://google-drive-link");

    MultipartFile multipartFile = mock(MultipartFile.class);

    when(multipartFile.getOriginalFilename()).thenReturn("test-file");
    when(multipartFile.getContentType()).thenReturn("text/plain");
    when(multipartFile.getBytes()).thenReturn("Hello world".getBytes());

    when(driveServiceMock.files()).thenReturn(filesMock);
    when(filesMock.create(any(File.class), any())).thenReturn(fileCreateMock);
    when(fileCreateMock.setSupportsAllDrives(true)).thenReturn(fileCreateMock);
    when(fileCreateMock.setFields("id, name, webViewLink")).thenReturn(fileCreateMock);
    when(fileCreateMock.execute()).thenReturn(expectedFile);

    when(driveServiceMock.permissions()).thenReturn(permissionsMock);
    when(permissionsMock.create(eq(expectedFile.getId()), any(Permission.class)))
        .thenReturn(permissionCreateMock);
    when(permissionCreateMock.setSupportsAllDrives(true)).thenReturn(permissionCreateMock);
    when(permissionCreateMock.execute()).thenReturn(new Permission());

    var googleDriveService = new GoogleDriveFileStorageRepository(driveServiceMock, properties);

    var actualFile = googleDriveService.uploadFile(multipartFile, FOLDER_ID_ROOT);

    assertThat(actualFile).isNotNull();
    assertThat(actualFile.id()).isEqualTo(expectedFile.getId());
    assertThat(actualFile.webLink()).isEqualTo(expectedFile.getWebViewLink());

    verify(permissionsMock).create(eq(expectedFile.getId()), any(Permission.class));
    verify(fileCreateMock).execute();
  }

  @Test
  @DisplayName(
      "Given upload failure from Google Drive API, when uploading file, then throw PlatformInternalException")
  void shouldThrowPlatformInternalExceptionWhenUploadFails() throws Exception {
    when(driveServiceMock.files()).thenReturn(filesMock);
    when(filesMock.create(any(File.class), any())).thenReturn(fileCreateMock);
    when(fileCreateMock.setSupportsAllDrives(true)).thenReturn(fileCreateMock);
    when(fileCreateMock.setFields("id, name, webViewLink")).thenReturn(fileCreateMock);
    when(fileCreateMock.execute()).thenThrow(new IOException("Test exception"));

    assertThatThrownBy(
            () -> service.uploadFile("test-file", "text/plain", new byte[] {}, FOLDER_ID_ROOT))
        .isInstanceOf(PlatformInternalException.class)
        .hasMessage("Failure to upload resources to google drive in respective folder id.");

    verify(fileCreateMock).execute();
  }

  @Test
  @DisplayName(
      "Given valid file ID, when deleting file, then execute delete with supportsAllDrives enabled")
  void shouldDeleteFileSuccessfully() throws Exception {
    Drive.Files.Delete fileDeleteMock = mock(Drive.Files.Delete.class);

    when(driveServiceMock.files()).thenReturn(filesMock);
    when(filesMock.delete("test-file-id")).thenReturn(fileDeleteMock);
    when(fileDeleteMock.setSupportsAllDrives(true)).thenReturn(fileDeleteMock);

    service.deleteFile("test-file-id");

    verify(fileDeleteMock).execute();
  }

  @Test
  @DisplayName(
      "Given drive API error on deletion, when deleting file, then throw PlatformInternalException")
  void shouldThrowPlatformInternalExceptionWhenDeleteFails() throws Exception {
    Drive.Files.Delete fileDeleteMock = mock(Drive.Files.Delete.class);

    when(driveServiceMock.files()).thenReturn(filesMock);
    when(filesMock.delete("invalid-file-id")).thenReturn(fileDeleteMock);
    when(fileDeleteMock.setSupportsAllDrives(true)).thenReturn(fileDeleteMock);
    doThrow(new IOException("Test exception")).when(fileDeleteMock).execute();

    assertThatThrownBy(() -> service.deleteFile("invalid-file-id"))
        .isInstanceOf(PlatformInternalException.class)
        .hasMessage("Failed to delete file from Google Drive");

    verify(fileDeleteMock).execute();
  }

  @Test
  @DisplayName(
      "Given file already deleted (404), when deleting file, then ignore error gracefully")
  void shouldIgnoreNotFoundWhenDeletingFile() throws Exception {
    Drive.Files.Delete fileDeleteMock = mock(Drive.Files.Delete.class);
    com.google.api.client.googleapis.json.GoogleJsonResponseException notFoundException =
        mock(com.google.api.client.googleapis.json.GoogleJsonResponseException.class);
    when(notFoundException.getStatusCode()).thenReturn(404);

    when(driveServiceMock.files()).thenReturn(filesMock);
    when(filesMock.delete("not-found-file-id")).thenReturn(fileDeleteMock);
    when(fileDeleteMock.setSupportsAllDrives(true)).thenReturn(fileDeleteMock);
    doThrow(notFoundException).when(fileDeleteMock).execute();

    service.deleteFile("not-found-file-id");

    verify(fileDeleteMock).execute();
  }

  @Test
  @DisplayName("Given valid file ID, when getting file, then return File metadata")
  void shouldGetFileSuccessfully() throws Exception {
    Drive.Files.Get fileGetMock = mock(Drive.Files.Get.class);
    File expectedFile = new File();
    expectedFile.setId("test-file-id");
    expectedFile.setName("test-file");
    expectedFile.setWebViewLink("http://google-drive-link");

    when(driveServiceMock.files()).thenReturn(filesMock);
    when(filesMock.get("test-file-id")).thenReturn(fileGetMock);
    when(fileGetMock.setSupportsAllDrives(true)).thenReturn(fileGetMock);
    when(fileGetMock.setFields("id, name, webViewLink")).thenReturn(fileGetMock);
    when(fileGetMock.execute()).thenReturn(expectedFile);

    File actualFile = service.getFile("test-file-id");

    assertThat(actualFile).isNotNull();
    assertThat(actualFile.getId()).isEqualTo(expectedFile.getId());
    assertThat(actualFile.getName()).isEqualTo(expectedFile.getName());
    assertThat(actualFile.getWebViewLink()).isEqualTo(expectedFile.getWebViewLink());
    verify(fileGetMock).execute();
  }

  @Test
  @DisplayName("Given drive API error on get, when getting file, then throw PlatformInternalException")
  void shouldThrowPlatformInternalExceptionWhenGetFails() throws Exception {
    Drive.Files.Get fileGetMock = mock(Drive.Files.Get.class);

    when(driveServiceMock.files()).thenReturn(filesMock);
    when(filesMock.get("invalid-file-id")).thenReturn(fileGetMock);
    when(fileGetMock.setSupportsAllDrives(true)).thenReturn(fileGetMock);
    when(fileGetMock.setFields("id, name, webViewLink")).thenReturn(fileGetMock);
    when(fileGetMock.execute()).thenThrow(new IOException("Test exception"));

    assertThatThrownBy(() -> service.getFile("invalid-file-id"))
        .isInstanceOf(PlatformInternalException.class)
        .hasMessage("Failed to get file from Google Drive");

    verify(fileGetMock).execute();
  }

  @Test
  @DisplayName("Given valid page size, when listing files, then return FileList")
  void shouldListFilesSuccessfully() throws Exception {
    Drive.Files.List fileListMock = mock(Drive.Files.List.class);
    FileList expectedFileList = new FileList();

    when(driveServiceMock.files()).thenReturn(filesMock);
    when(filesMock.list()).thenReturn(fileListMock);
    when(fileListMock.setSupportsAllDrives(true)).thenReturn(fileListMock);
    when(fileListMock.setIncludeItemsFromAllDrives(true)).thenReturn(fileListMock);
    when(fileListMock.setPageSize(10)).thenReturn(fileListMock);
    when(fileListMock.setFields("nextPageToken, files(id, name, webViewLink)"))
        .thenReturn(fileListMock);
    when(fileListMock.execute()).thenReturn(expectedFileList);

    FileList actualFileList = service.listFiles(10);

    assertThat(actualFileList).isNotNull();
    verify(fileListMock).execute();
  }

  @Test
  @DisplayName("Given drive API error on list, when listing files, then throw PlatformInternalException")
  void shouldThrowPlatformInternalExceptionWhenListFails() throws Exception {
    Drive.Files.List fileList = mock(Drive.Files.List.class);

    when(driveServiceMock.files()).thenReturn(filesMock);
    when(filesMock.list()).thenReturn(fileList);
    when(fileList.setSupportsAllDrives(true)).thenReturn(fileList);
    when(fileList.setIncludeItemsFromAllDrives(true)).thenReturn(fileList);
    when(fileList.setPageSize(10)).thenReturn(fileList);
    when(fileList.setFields("nextPageToken, files(id, name, webViewLink)")).thenReturn(fileList);
    when(fileList.execute()).thenThrow(new IOException("Test exception"));

    assertThatThrownBy(() -> service.listFiles(10))
        .isInstanceOf(PlatformInternalException.class)
        .hasMessage("Failed to list files from Google Drive");

    verify(fileList).execute();
  }
}
