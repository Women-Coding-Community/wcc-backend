package com.wcc.platform.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for Google Drive integration.
 *
 * <p>The {@code credentialsJson} field holds the full JSON content of a Google service account key.
 * In production (Fly.io), set the {@code GOOGLE_DRIVE_CREDENTIALS_JSON} environment variable to
 * this JSON content — no file needs to be bundled into the Docker image.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "storage.google-drive")
public class GoogleDriveConfig {

  /**
   * Full JSON content of the Google service account key. Mapped from the {@code
   * GOOGLE_DRIVE_CREDENTIALS_JSON} environment variable.
   */
  private String credentialsJson;
}
