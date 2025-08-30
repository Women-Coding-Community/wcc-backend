package com.wcc.platform.domain.platform.type;

import com.wcc.platform.properties.FolderStorageProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/** Enum representing the different types of resources that can be stored. */
@Getter
@AllArgsConstructor
public enum ResourceType {
  PROFILE_PICTURE(1),
  EVENT_IMAGE(2),
  EVENT_PDF(3),
  EVENT_PRESENTATION(4),
  OTHER(5),
  MENTOR_RESOURCE(6),
  IMAGE(7),
  RESOURCE(8);

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
      case EVENT_PDF, EVENT_IMAGE, EVENT_PRESENTATION ->
          StringUtils.trimToEmpty(properties.getEventsFolder());
      case MENTOR_RESOURCE -> StringUtils.trimToEmpty(properties.getMentorsFolder());
      case IMAGE -> StringUtils.trimToEmpty(properties.getImagesFolder());
      default -> StringUtils.trimToEmpty(properties.getResourcesFolder());
    };
  }
}
