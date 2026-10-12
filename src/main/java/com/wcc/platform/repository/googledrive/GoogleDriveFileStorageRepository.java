package com.wcc.platform.repository.googledrive;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.wcc.platform.configuration.GoogleDriveConfig;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.domain.platform.filestorage.FileStorageItem;
import com.wcc.platform.domain.platform.filestorage.FileStoragePage;
import com.wcc.platform.domain.platform.filestorage.FileStored;
import com.wcc.platform.properties.FolderStorageProperties;
import com.wcc.platform.repository.FileStorageRepository;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/** Service for interacting with Google Drive API. */
@Slf4j
@Service
@ConditionalOnProperty(
    prefix = "storage",
    name = "type",
    havingValue = "google",
    matchIfMissing = true)
public class GoogleDriveFileStorageRepository implements FileStorageRepository {

  private final Drive driveService;
  private final FolderStorageProperties folders;

  /** Constructor with dependencies. */
  public GoogleDriveFileStorageRepository(
      final Drive driveService, final FolderStorageProperties folders) {
    this.driveService = driveService;
    this.folders = folders;
  }

  /** Spring constructor: builds the Drive client using service account credentials. */
  @Autowired
  public GoogleDriveFileStorageRepository(
      final FolderStorageProperties folders, final GoogleDriveConfig googleDriveConfig)
      throws GeneralSecurityException, IOException {
    this.driveService = GoogleDriveCredentialLoader.buildDriveService(googleDriveConfig);
    this.folders = folders;
  }

  @Override
  public FolderStorageProperties getFolders() {
    return folders;
  }

  /**
   * Uploads a file to Google Drive.
   *
   * @param fileName Name of the file
   * @param contentType MIME type of the file
   * @param fileData File data as byte array
   * @param folder folder-id from google drive.
   * @return Google Drive file information
   */
  @Override
  public FileStored uploadFile(
      final String fileName, final String contentType, final byte[] fileData, final String folder) {
    try {
      final var fileMetadata = new File();
      fileMetadata.setName(fileName);
      if (StringUtils.isBlank(folder)) {
        fileMetadata.setParents(Collections.singletonList(folders.getMainFolder()));
        log.warn("folder-id is blank; " + "uploading to My Drive root without specifying parents.");
      } else {
        fileMetadata.setParents(Collections.singletonList(folder));
      }

      final var mediaContent =
          new InputStreamContent(contentType, new ByteArrayInputStream(fileData));

      final var file =
          files()
              .create(fileMetadata, mediaContent)
              .setSupportsAllDrives(true)
              .setFields("id, name, webViewLink")
              .execute();

      final var permission = new Permission().setType("anyone").setRole("reader");

      permissions().create(file.getId(), permission).setSupportsAllDrives(true).execute();

      return new FileStored(file.getId(), file.getWebViewLink());
    } catch (IOException e) {
      throw new PlatformInternalException(
          "Failure to upload resources to google drive in respective folder id.", e);
    }
  }

  /** Uploads a file to a specific Google Drive folder. */
  @Override
  public FileStored uploadFile(final MultipartFile file, final String folderId) {
    try {
      return uploadFile(
          file.getOriginalFilename(), file.getContentType(), file.getBytes(), folderId);
    } catch (IOException e) {
      throw new PlatformInternalException(
          "Failure to upload resources to google drive in respective folder id.", e);
    }
  }

  /** Deletes a file from Google Drive. */
  @Override
  public void deleteFile(final String fileId) {
    try {
      files().delete(fileId).setSupportsAllDrives(true).execute();
    } catch (com.google.api.client.googleapis.json.GoogleJsonResponseException e) {
      if (e.getStatusCode() == org.springframework.http.HttpStatus.NOT_FOUND.value()) {
        log.warn("File {} not found in Google Drive when attempting to delete; skipping.", fileId);
        return;
      }
      throw new PlatformInternalException("Failed to delete file from Google Drive", e);
    } catch (IOException e) {
      throw new PlatformInternalException("Failed to delete file from Google Drive", e);
    }
  }

  /** Gets a file from Google Drive. */
  public FileStorageItem getFile(final String fileId) {
    try {
      final var file =
          files()
              .get(fileId)
              .setSupportsAllDrives(true)
              .setFields("id, name, webViewLink")
              .execute();
      return new FileStorageItem(file.getId(), file.getName(), file.getWebViewLink());
    } catch (IOException e) {
      throw new PlatformInternalException("Failed to get file from Google Drive", e);
    }
  }

  /** Lists files in Google Drive. */
  public FileStoragePage listFiles(final int pageSize) {
    try {
      final var fileList =
          files()
              .list()
              .setSupportsAllDrives(true)
              .setIncludeItemsFromAllDrives(true)
              .setPageSize(pageSize)
              .setFields("nextPageToken, files(id, name, webViewLink)")
              .execute();

      final var items =
          fileList.getFiles().stream()
              .map(f -> new FileStorageItem(f.getId(), f.getName(), f.getWebViewLink()))
              .collect(Collectors.toList());

      return new FileStoragePage(items, fileList.getNextPageToken());
    } catch (IOException e) {
      log.error("Failed to list files from Google Drive", e);
      throw new PlatformInternalException("Failed to list files from Google Drive", e);
    }
  }

  private Drive.Files files() {
    return driveService.files();
  }

  private Drive.Permissions permissions() {
    return driveService.permissions();
  }
}
