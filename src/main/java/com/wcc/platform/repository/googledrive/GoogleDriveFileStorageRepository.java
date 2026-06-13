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
import com.wcc.platform.configuration.GoogleDriveConfig;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.domain.platform.filestorage.FileStored;
import com.wcc.platform.properties.FolderStorageProperties;
import com.wcc.platform.repository.FileStorageRepository;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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

  private final Drive driveService;

  private final FolderStorageProperties folders;

  /** Constructor with dependencies. */
  public GoogleDriveFileStorageRepository(
      final Drive driveService, final FolderStorageProperties folders) {
    this.driveService = driveService;
    this.folders = folders;
  }

  /**
   * Spring constructor: builds the Drive client using service account credentials supplied via
   * {@link GoogleDriveConfig#getCredentialsJson()}, which is mapped from the {@code
   * GOOGLE_DRIVE_CREDENTIALS_JSON} environment variable.
   */
  @Autowired
  public GoogleDriveFileStorageRepository(
      final FolderStorageProperties folders, final GoogleDriveConfig googleDriveConfig)
      throws GeneralSecurityException, IOException {
    final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    this.driveService =
        new Drive.Builder(
                httpTransport,
                JSON_FACTORY,
                loadServiceAccountCredentials(googleDriveConfig.getCredentialsJson()))
            .setApplicationName(APPLICATION_NAME)
            .build();
    this.folders = folders;
  }

  /**
   * Loads Google Drive service account credentials from a JSON string.
   *
   * <p>The JSON content is expected to be the full service account key file, provided via the
   * {@code GOOGLE_DRIVE_CREDENTIALS_JSON} environment variable.
   *
   * @param credentialsJson the service account key JSON as a string
   * @return an {@link HttpCredentialsAdapter} wrapping the scoped credentials
   * @throws IOException if the JSON is blank or cannot be parsed
   * @throws IllegalStateException if {@code credentialsJson} is blank
   */
  private static HttpCredentialsAdapter loadServiceAccountCredentials(final String credentialsJson)
      throws IOException {
    if (StringUtils.isBlank(credentialsJson)) {
      throw new IllegalStateException(
          "Google Drive credentials are not configured. "
              + "Set the GOOGLE_DRIVE_CREDENTIALS_JSON environment variable.");
    }
    try (InputStream in =
        new ByteArrayInputStream(credentialsJson.getBytes(StandardCharsets.UTF_8))) {
      final GoogleCredentials credentials = GoogleCredentials.fromStream(in).createScoped(SCOPES);
      log.info("Loaded Google Drive service account credentials from environment.");
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
