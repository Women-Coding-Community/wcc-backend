package com.wcc.platform.repository.googledrive;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.wcc.platform.configuration.GoogleDriveConfig;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/** Utility class for loading Google Drive credentials and building the Drive service. */
@Slf4j
public final class GoogleDriveCredentialLoader {

  public static final String APPLICATION_NAME = "WCC Backend";
  public static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);

  private GoogleDriveCredentialLoader() {}

  /**
   * Builds a Google Drive service using the provided configuration.
   *
   * @param googleDriveConfig Configuration properties for Google Drive.
   * @return A configured Drive service instance.
   * @throws GeneralSecurityException If security setup fails.
   * @throws IOException If I/O error occurs.
   */
  public static Drive buildDriveService(final GoogleDriveConfig googleDriveConfig)
      throws GeneralSecurityException, IOException {
    final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    final HttpCredentialsAdapter credentials =
        loadServiceAccountCredentials(googleDriveConfig.getCredentialsJson());

    return new Drive.Builder(
            httpTransport,
            JSON_FACTORY,
            request -> {
              credentials.initialize(request);
              request.setConnectTimeout(googleDriveConfig.getConnectTimeoutMs());
              request.setReadTimeout(googleDriveConfig.getReadTimeoutMs());
            })
        .setApplicationName(APPLICATION_NAME)
        .build();
  }

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
}
