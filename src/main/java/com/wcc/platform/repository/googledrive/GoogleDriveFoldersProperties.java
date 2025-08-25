package com.wcc.platform.repository.googledrive;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** Binds Google Drive folder IDs from application.yml under google.drive.folders.* */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "google.drive.folders")
public class GoogleDriveFoldersProperties {
  /** Folder for general resources. */
  private String resources;

  /** Folder for mentors resources. */
  private String mentorResources;

  /** Folder for mentor profile pictures. */
  private String mentorProfilePicture;

  /** Folder for events. */
  private String events;
}
