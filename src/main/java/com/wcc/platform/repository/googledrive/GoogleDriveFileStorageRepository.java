package com.wcc.platform.repository.googledrive;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.domain.platform.filestorage.FileStored;
import com.wcc.platform.properties.FolderStorageProperties;
import com.wcc.platform.repository.FileStorageRepository;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
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
@SuppressWarnings({"PMD.LooseCoupling", "PMD.ExcessiveImports"})
public class GoogleDriveFileStorageRepository implements FileStorageRepository {

  private static final String APPLICATION_NAME = "WCC Backend";
  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
  private static final String SERVICE_ACCOUNT_PATH = "/service-account.json";

  private final Drive driveService;

  private final FolderStorageProperties folders;

  /** Constructor with dependencies. */
  public GoogleDriveFileStorageRepository(
      final Drive driveService, final FolderStorageProperties folders) {
    this.driveService = driveService;
    this.folders = folders;
  }

  /** Spring constructor: builds Drive client using service account credentials. */
  @Autowired
  public GoogleDriveFileStorageRepository(final FolderStorageProperties folders)
      throws GeneralSecurityException, IOException {
    final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    this.driveService =
        new Drive.Builder(httpTransport, JSON_FACTORY, loadServiceAccountCredentials())
            .setApplicationName(APPLICATION_NAME)
            .build();
    this.folders = folders;
  }

  /**
   * Loads Google Drive credentials from a service account JSON file.
   *
   * @return An {@link HttpCredentialsAdapter} wrapping the service account credentials.
   * @throws IOException If the service account file cannot be found or read.
   */
  private static HttpCredentialsAdapter loadServiceAccountCredentials() throws IOException {
    try (InputStream in =
        GoogleDriveFileStorageRepository.class.getResourceAsStream(SERVICE_ACCOUNT_PATH)) {
      if (in == null) {
        throw new FileNotFoundException("Resource not found: " + SERVICE_ACCOUNT_PATH);
      }
      final GoogleCredentials credentials =
          GoogleCredentials.fromStream(in).createScoped(SCOPES);
      log.info("Loaded Google Drive service account credentials.");
      return new HttpCredentialsAdapter(credentials);
    }
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
      files().delete(fileId).execute();
    } catch (IOException e) {
      throw new PlatformInternalException("Failed to delete file from Google Drive", e);
    }
  }

  /** Gets a file from Google Drive. */
  public File getFile(final String fileId) {
    try {
      return files().get(fileId).setFields("id, name, webViewLink").execute();
    } catch (IOException e) {
      throw new PlatformInternalException("Failed to get file from Google Drive", e);
    }
  }

  /** Lists files in Google Drive. */
  public FileList listFiles(final int pageSize) {
    try {
      return files()
          .list()
          .setPageSize(pageSize)
          .setFields("nextPageToken, files(id, name, webViewLink)")
          .execute();
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
