package com.wcc.platform.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** Binds to folder IDs from application.yml.* */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "storage.folders")
public class FolderStorageProperties {

  /** Folder for general resources. */
  private String mainFolder;

  /** Folder for general resources. */
  private String resourcesFolder;

  /** Folder for mentors resources. */
  private String mentorsFolder;

  /** Folder for mentor profile pictures. */
  private String mentorsProfileFolder;

  /** Folder for general resources of events. */
  private String eventsFolder;

  /** Folder for general images. */
  private String imagesFolder;
}
