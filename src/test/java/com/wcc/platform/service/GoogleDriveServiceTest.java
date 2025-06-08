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
import java.io.IOException;
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

    GoogleDriveService googleDriveService =
        new GoogleDriveService(driveServiceMock, FOLDER_ID_ROOT);

    File actualFile =
        googleDriveService.uploadFile("test-file", "text/plain", "Hello world".getBytes());

    assertNotNull(actualFile);
    assertEquals(expectedFile.getId(), actualFile.getId());
    assertEquals(expectedFile.getName(), actualFile.getName());
    assertEquals(expectedFile.getWebViewLink(), actualFile.getWebViewLink());

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

    GoogleDriveService googleDriveService =
        new GoogleDriveService(driveServiceMock, FOLDER_ID_ROOT);

    File actualFile = googleDriveService.uploadFile(multipartFile);

    assertNotNull(actualFile);
    assertEquals(expectedFile.getId(), actualFile.getId());
    assertEquals(expectedFile.getName(), actualFile.getName());
    assertEquals(expectedFile.getWebViewLink(), actualFile.getWebViewLink());

    verify(permissionsMock).create(eq(expectedFile.getId()), any(Permission.class));
    verify(fileCreateMock).execute();
  }

  @Test
  void testUploadFileThrowsException() throws Exception {

    when(driveServiceMock.files()).thenReturn(filesMock);
    when(filesMock.create(any(File.class), any())).thenReturn(fileCreateMock);
    when(fileCreateMock.setFields("id, name, webViewLink")).thenReturn(fileCreateMock);
    when(fileCreateMock.execute()).thenThrow(new IOException("Test exception"));

    GoogleDriveService googleDriveService =
        new GoogleDriveService(driveServiceMock, FOLDER_ID_ROOT);

    PlatformInternalException exception =
        assertThrows(
            PlatformInternalException.class,
            () ->
                googleDriveService.uploadFile("test-file", "text/plain", "Hello world".getBytes()));

    assertEquals("Failed to upload file to Google Drive", exception.getMessage());
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

    GoogleDriveService googleDriveService =
        new GoogleDriveService(driveServiceMock, FOLDER_ID_ROOT);

    File actualFile = googleDriveService.getFile("test-file-id");

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

    GoogleDriveService googleDriveService =
        new GoogleDriveService(driveServiceMock, FOLDER_ID_ROOT);

    PlatformInternalException exception =
        assertThrows(
            PlatformInternalException.class, () -> googleDriveService.getFile("invalid-file-id"));

    assertEquals("Failed to get file from Google Drive", exception.getMessage());
    verify(fileGetMock).execute();
  }

  @Test
  void testDeleteFileThrowsException() throws Exception {
    Drive driveServiceMock = mock(Drive.class);
    Drive.Files filesMock = mock(Drive.Files.class);
    Drive.Files.Delete fileDeleteMock = mock(Drive.Files.Delete.class);

    when(driveServiceMock.files()).thenReturn(filesMock);
    when(filesMock.delete("invalid-file-id")).thenReturn(fileDeleteMock);
    when(fileDeleteMock.execute()).thenThrow(new IOException("Test exception"));

    GoogleDriveService googleDriveService =
        new GoogleDriveService(driveServiceMock, FOLDER_ID_ROOT);

    PlatformInternalException exception =
        assertThrows(
            PlatformInternalException.class,
            () -> googleDriveService.deleteFile("invalid-file-id"));

    assertEquals("Failed to delete file from Google Drive", exception.getMessage());
    verify(fileDeleteMock).execute();
  }

  @Test
  void testListFilesSuccess() throws Exception {
    Drive driveServiceMock = mock(Drive.class);
    Drive.Files filesMock = mock(Drive.Files.class);
    Drive.Files.List fileListMock = mock(Drive.Files.List.class);
    FileList expectedFileList = new FileList();

    when(driveServiceMock.files()).thenReturn(filesMock);
    when(filesMock.list()).thenReturn(fileListMock);
    when(fileListMock.setPageSize(10)).thenReturn(fileListMock);
    when(fileListMock.setFields("nextPageToken, files(id, name, webViewLink)"))
        .thenReturn(fileListMock);
    when(fileListMock.execute()).thenReturn(expectedFileList);

    GoogleDriveService googleDriveService =
        new GoogleDriveService(driveServiceMock, FOLDER_ID_ROOT);

    FileList actualFileList = googleDriveService.listFiles(10);

    assertNotNull(actualFileList);
    verify(fileListMock).execute();
  }

  @Test
  void testListFilesThrowsException() throws Exception {
    Drive driveServiceMock = mock(Drive.class);
    Drive.Files filesMock = mock(Drive.Files.class);
    Drive.Files.List fileListMock = mock(Drive.Files.List.class);

    when(driveServiceMock.files()).thenReturn(filesMock);
    when(filesMock.list()).thenReturn(fileListMock);
    when(fileListMock.setPageSize(10)).thenReturn(fileListMock);
    when(fileListMock.setFields("nextPageToken, files(id, name, webViewLink)"))
        .thenReturn(fileListMock);
    when(fileListMock.execute()).thenThrow(new IOException("Test exception"));

    GoogleDriveService googleDriveService =
        new GoogleDriveService(driveServiceMock, FOLDER_ID_ROOT);

    PlatformInternalException exception =
        assertThrows(PlatformInternalException.class, () -> googleDriveService.listFiles(10));

    assertEquals("Failed to list files from Google Drive", exception.getMessage());
    verify(fileListMock).execute();
  }
}
