package com.wcc.platform.domain.resource;

import com.wcc.platform.properties.FolderStorageProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/** Enum representing the different types of resources that can be stored. */
@Getter
@AllArgsConstructor
public enum ResourceType {
  OTHER(0),
  PROFILE_PICTURE(1),
  EVENT_IMAGE(2),
  EVENT_PDF(3),
  EVENT_PRESENTATION(4),
  MENTOR_RESOURCES(5),
  IMAGES(6);

  private final int resourceTypeId;

  /**
   * Converts the current resource type to its corresponding folder ID based on the given folder
   * storage properties. The folder ID returned is determined by the resource type and its mapping
   * within the FolderStorageProperties.
   *
   * @param properties the folder storage properties that define the folder mappings for various
   *     resource types
   * @return the folder ID corresponding to the resource type, or an empty string if the mapping is
   *     undefined
   */
  public String toFolderId(final FolderStorageProperties properties) {
    return switch (this) {
      case PROFILE_PICTURE -> StringUtils.trimToEmpty(properties.getMentorsProfileFolder());
      case EVENT_PDF, EVENT_PRESENTATION -> StringUtils.trimToEmpty(properties.getEventsFolder());
      case MENTOR_RESOURCES -> StringUtils.trimToEmpty(properties.getMentorsFolder());
      case IMAGES -> StringUtils.trimToEmpty(properties.getImagesFolder());
      default -> StringUtils.trimToEmpty(properties.getResourcesFolder());
    };
  }
}
