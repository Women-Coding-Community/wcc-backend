package com.wcc.platform.repository.googledrive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.domain.platform.filestorage.FileStored;
import com.wcc.platform.properties.FolderStorageProperties;
import com.wcc.platform.repository.FileStorageRepository;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/** Service for interacting with Google Drive API. */
@Slf4j
@Service
@SuppressWarnings({"PMD.LooseCoupling", "PMD.ExcessiveImports"})
public class GoogleDriveRepository implements FileStorageRepository {

  private static final String APPLICATION_NAME = "WCC Backend";
  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
  private static final String CREDS_FILE_PATH = "/credentials.json";
  private static final String TOKENS_DIR_PATH = "tokens";

  private final Drive driveService;

  private final FolderStorageProperties folders;

  /** Constructor with dependencies. */
  public GoogleDriveRepository(final Drive driveService, final FolderStorageProperties folders) {
    this.driveService = driveService;
    this.folders = folders;
  }

  /** Spring constructor: builds Drive client and reads folders from properties. */
  @Autowired
  public GoogleDriveRepository(final FolderStorageProperties folders)
      throws GeneralSecurityException, IOException {
    final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    this.driveService =
        new Drive.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport))
            .setApplicationName(APPLICATION_NAME)
            .build();
    this.folders = folders;
  }

  /** Constructor that initializes the Google Drive service (no Spring). */
  public GoogleDriveRepository() throws GeneralSecurityException, IOException {
    final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    this.driveService =
        new Drive.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport))
            .setApplicationName(APPLICATION_NAME)
            .build();
    this.folders = new FolderStorageProperties();
  }

  /**
   * Creates an authorized Credential object.
   *
   * @param httpTransport The network HTTP Transport.
   * @return An authorized Credential object.
   * @throws IOException If the credentials.json file cannot be found.
   */
  private static Credential getCredentials(final NetHttpTransport httpTransport)
      throws IOException {
    try (InputStream in = GoogleDriveRepository.class.getResourceAsStream(CREDS_FILE_PATH)) {
      if (in == null) {
        throw new FileNotFoundException("Resource not found: " + CREDS_FILE_PATH);
      }
      final var clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
      final String clientId = clientSecrets.getDetails().getClientId();
      final String userKey = "user-" + (clientId == null ? "unknown" : clientId);

      final var flow =
          new GoogleAuthorizationCodeFlow.Builder(
                  httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
              .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIR_PATH)))
              .setAccessType("offline")
              .build();

      final Credential credential = flow.loadCredential(userKey);
      if (credential != null) {
        log.info(
            "Using existing Google Drive credentials from '{}' for clientId '{}'. "
                + "No browser authorization needed.",
            TOKENS_DIR_PATH,
            clientId);
        return credential;
      }

      log.info(
          "No existing credentials found for clientId '{}'. Opening browser for authorization...",
          clientId);
      final LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
      return new AuthorizationCodeInstalledApp(flow, receiver).authorize(userKey);
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
          files().create(fileMetadata, mediaContent).setFields("id, name, webViewLink").execute();

      final var permission = new Permission().setType("anyone").setRole("reader");

      permissions().create(file.getId(), permission).execute();

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
