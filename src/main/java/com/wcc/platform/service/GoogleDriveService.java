package com.wcc.platform.service;

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
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/** Service for interacting with Google Drive API. */
@Service
public class GoogleDriveService {

  private static final String APPLICATION_NAME = "WCC Backend";
  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
  private static final String CREDS_FILE_PATH = "/credentials.json";
  private static final String TOKENS_DIR_PATH = "tokens";
  private final Drive driveService;

  private final String folderIdRoot;

  /** Constructor with dependencies. */
  public GoogleDriveService(
      final Drive driveService, @Value("${google.drive.folder-id}") final String folderIdRoot) {
    this.driveService = driveService;
    this.folderIdRoot = folderIdRoot;
  }

  /** Constructor that initializes the Google Drive service. */
  public GoogleDriveService() throws GeneralSecurityException, IOException {
    final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    this.driveService =
        new Drive.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport))
            .setApplicationName(APPLICATION_NAME)
            .build();
    this.folderIdRoot = "";
  }

  /**
   * Creates an authorized Credential object.
   *
   * @param httpTransport The network HTTP Transport.
   * @return An authorized Credential object.
   * @throws IOException If the credentials.json file cannot be found.
   */
  private Credential getCredentials(final NetHttpTransport httpTransport) throws IOException {
    // Load client secrets.
    try (final InputStream in =
        GoogleDriveService.class.getResourceAsStream(CREDS_FILE_PATH)) {
      if (in == null) {
        throw new FileNotFoundException("Resource not found: " + CREDS_FILE_PATH);
      }
      final GoogleClientSecrets clientSecrets =
          GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

      // Build flow and trigger the user authorization request.
      final GoogleAuthorizationCodeFlow flow =
          new GoogleAuthorizationCodeFlow.Builder(
                  httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
              .setDataStoreFactory(
                  new FileDataStoreFactory(new java.io.File(TOKENS_DIR_PATH)))
              .setAccessType("offline")
              .build();
      final LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
      return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
  }

  /**
   * Uploads a file to Google Drive.
   *
   * @param fileName Name of the file
   * @param contentType MIME type of the file
   * @param fileData File data as byte array
   * @return Google Drive file information
   */
  public File uploadFile(final String fileName, final String contentType, final byte[] fileData) {
    try {
      final File fileMetadata = new File();
      fileMetadata.setName(fileName);
      fileMetadata.setParents(Collections.singletonList(folderIdRoot));

      final InputStreamContent mediaContent =
          new InputStreamContent(contentType, new ByteArrayInputStream(fileData));

      final File file =
          driveService
              .files()
              .create(fileMetadata, mediaContent)
              .setFields("id, name, webViewLink")
              .execute();

      // Set file permission to be publicly accessible
      final Permission permission = new Permission().setType("anyone").setRole("reader");

      driveService.permissions().create(file.getId(), permission).execute();

      return file;
    } catch (IOException e) {
      throw new PlatformInternalException("Failed to upload file to Google Drive", e);
    }
  }

  /** Uploads a file to Google Drive. */
  public File uploadFile(final MultipartFile file) {
    try {
      return uploadFile(file.getOriginalFilename(), file.getContentType(), file.getBytes());
    } catch (IOException e) {
      throw new PlatformInternalException("Failed to read file data", e);
    }
  }

  /** Deletes a file from Google Drive. */
  public void deleteFile(final String fileId) {
    try {
      driveService.files().delete(fileId).execute();
    } catch (IOException e) {
      throw new PlatformInternalException("Failed to delete file from Google Drive", e);
    }
  }

  /** Gets a file from Google Drive. */
  public File getFile(final String fileId) {
    try {
      return driveService.files().get(fileId).setFields("id, name, webViewLink").execute();
    } catch (IOException e) {
      throw new PlatformInternalException("Failed to get file from Google Drive", e);
    }
  }

  /** Lists files in Google Drive. */
  public FileList listFiles(final int pageSize) {
    try {
      return driveService
          .files()
          .list()
          .setPageSize(pageSize)
          .setFields("nextPageToken, files(id, name, webViewLink)")
          .execute();
    } catch (IOException e) {
      throw new PlatformInternalException("Failed to list files from Google Drive", e);
    }
  }
}
