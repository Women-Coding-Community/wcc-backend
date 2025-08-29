package com.wcc.platform.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.properties.FolderStorageProperties;
import com.wcc.platform.repository.googledrive.GoogleDriveService;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class GoogleDriveServiceTest {

  private static final String FOLDER_ID_ROOT = "test-folder-id";
  private final Drive driveServiceMock = mock(Drive.class);
  private final Drive.Files filesMock = mock(Drive.Files.class);
  private final Drive.Files.Create fileCreateMock = mock(Drive.Files.Create.class);
  private final Drive.Permissions permissionsMock = mock(Drive.Permissions.class);
  private final Drive.Permissions.Create permissionCreateMock =
      mock(Drive.Permissions.Create.class);

  private FolderStorageProperties properties;
  private GoogleDriveService service;

  @BeforeEach
  void setUp() {
    properties = new FolderStorageProperties();
    properties.setMainFolder(FOLDER_ID_ROOT);

    service = new GoogleDriveService(driveServiceMock, properties);
  }

  @Test
  void testUploadFileSuccess() throws Exception {
    File expectedFile = new File();
    expectedFile.setId("test-file-id");
    expectedFile.setName("test-file");
    expectedFile.setWebViewLink("http://google-drive-link");

    when(driveServiceMock.files()).thenReturn(filesMock);
    when(filesMock.create(any(File.class), any())).thenReturn(fileCreateMock);
    when(fileCreateMock.setFields("id, name, webViewLink")).thenReturn(fileCreateMock);
    when(fileCreateMock.execute()).thenReturn(expectedFile);

    when(driveServiceMock.permissions()).thenReturn(permissionsMock);
    when(permissionsMock.create(eq(expectedFile.getId()), any(Permission.class)))
        .thenReturn(permissionCreateMock);
    when(permissionCreateMock.execute()).thenReturn(new Permission());

    var actualFile =
        service.uploadFile("test-file", "text/plain", "Hello world".getBytes(), FOLDER_ID_ROOT);

    assertNotNull(actualFile);
    assertEquals(expectedFile.getId(), actualFile.id());
    assertEquals(expectedFile.getWebViewLink(), actualFile.webLink());

    verify(permissionsMock).create(eq(expectedFile.getId()), any(Permission.class));
    verify(fileCreateMock).execute();
  }

  @Test
  void testUploadFileMultipartFileSuccess() throws Exception {
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
    when(fileCreateMock.setFields("id, name, webViewLink")).thenReturn(fileCreateMock);
    when(fileCreateMock.execute()).thenReturn(expectedFile);

    when(driveServiceMock.permissions()).thenReturn(permissionsMock);
    when(permissionsMock.create(eq(expectedFile.getId()), any(Permission.class)))
        .thenReturn(permissionCreateMock);
    when(permissionCreateMock.execute()).thenReturn(new Permission());

    var googleDriveService = new GoogleDriveService(driveServiceMock, properties);

    var actualFile = googleDriveService.uploadFile(multipartFile, FOLDER_ID_ROOT);

    assertNotNull(actualFile);
    assertEquals(expectedFile.getId(), actualFile.id());
    assertEquals(expectedFile.getWebViewLink(), actualFile.webLink());

    verify(permissionsMock).create(eq(expectedFile.getId()), any(Permission.class));
    verify(fileCreateMock).execute();
  }

  @Test
  void testUploadFileThrowsException() throws Exception {

    when(driveServiceMock.files()).thenReturn(filesMock);
    when(filesMock.create(any(File.class), any())).thenReturn(fileCreateMock);
    when(fileCreateMock.setFields("id, name, webViewLink")).thenReturn(fileCreateMock);
    when(fileCreateMock.execute()).thenThrow(new IOException("Test exception"));

    PlatformInternalException exception =
        assertThrows(
            PlatformInternalException.class,
            () -> service.uploadFile("test-file", "text/plain", new byte[] {}, FOLDER_ID_ROOT));

    assertEquals("Failure to create permission to file in google drive", exception.getMessage());
    verify(fileCreateMock).execute();
  }

  @Test
  void testGetFileSuccess() throws Exception {
    Drive.Files.Get fileGetMock = mock(Drive.Files.Get.class);
    File expectedFile = new File();
    expectedFile.setId("test-file-id");
    expectedFile.setName("test-file");
    expectedFile.setWebViewLink("http://google-drive-link");

    when(driveServiceMock.files()).thenReturn(filesMock);
    when(filesMock.get("test-file-id")).thenReturn(fileGetMock);
    when(fileGetMock.setFields("id, name, webViewLink")).thenReturn(fileGetMock);
    when(fileGetMock.execute()).thenReturn(expectedFile);

    File actualFile = service.getFile("test-file-id");

    assertNotNull(actualFile);
    assertEquals(expectedFile.getId(), actualFile.getId());
    assertEquals(expectedFile.getName(), actualFile.getName());
    assertEquals(expectedFile.getWebViewLink(), actualFile.getWebViewLink());
    verify(fileGetMock).execute();
  }

  @Test
  void testGetFileThrowsException() throws Exception {
    Drive.Files.Get fileGetMock = mock(Drive.Files.Get.class);

    when(driveServiceMock.files()).thenReturn(filesMock);
    when(filesMock.get("invalid-file-id")).thenReturn(fileGetMock);
    when(fileGetMock.setFields("id, name, webViewLink")).thenReturn(fileGetMock);
    when(fileGetMock.execute()).thenThrow(new IOException("Test exception"));

    PlatformInternalException exception =
        assertThrows(PlatformInternalException.class, () -> service.getFile("invalid-file-id"));

    assertEquals("Failed to get file from Google Drive", exception.getMessage());
    verify(fileGetMock).execute();
  }

  @Test
  void testListFilesSuccess() throws Exception {
    Drive driveService = mock(Drive.class);
    Drive.Files files = mock(Drive.Files.class);
    Drive.Files.List fileListMock = mock(Drive.Files.List.class);
    FileList expectedFileList = new FileList();

    when(driveService.files()).thenReturn(files);
    when(files.list()).thenReturn(fileListMock);
    when(fileListMock.setPageSize(10)).thenReturn(fileListMock);
    when(fileListMock.setFields("nextPageToken, files(id, name, webViewLink)"))
        .thenReturn(fileListMock);
    when(fileListMock.execute()).thenReturn(expectedFileList);

    FileList actualFileList = service.listFiles(10);

    assertNotNull(actualFileList);
    verify(fileListMock).execute();
  }

  @Test
  void testListFilesThrowsException() throws Exception {
    Drive driveService = mock(Drive.class);
    Drive.Files files = mock(Drive.Files.class);
    Drive.Files.List fileList = mock(Drive.Files.List.class);

    when(driveService.files()).thenReturn(files);
    when(files.list()).thenReturn(fileList);
    when(fileList.setPageSize(10)).thenReturn(fileList);
    when(fileList.setFields("nextPageToken, files(id, name, webViewLink)")).thenReturn(fileList);
    when(fileList.execute()).thenThrow(new IOException("Test exception"));

    PlatformInternalException exception =
        assertThrows(PlatformInternalException.class, () -> service.listFiles(10));

    assertEquals("Failed to list files from Google Drive", exception.getMessage());
    verify(fileList).execute();
  }
}
