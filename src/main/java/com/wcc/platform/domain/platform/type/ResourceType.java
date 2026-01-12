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
  RESOURCE(8),
  MENTORSHIP(9);

  private final int resourceTypeId;

  /**
   * Retrieves the corresponding {@code ResourceType} enum value based on a given resource type ID.
   * If no match is found, the default {@code RESOURCE} type is returned.
   *
   * @param resourceTypeId the integer ID representing a specific {@code ResourceType}
   * @return the {@code ResourceType} that matches the given ID, or {@code RESOURCE} if no match is
   *     found
   */
  public static ResourceType fromId(final int resourceTypeId) {
    for (final ResourceType type : values()) {
      if (type.getResourceTypeId() == resourceTypeId) {
        return type;
      }
    }
    return RESOURCE;
  }

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
      case MENTORSHIP -> StringUtils.trimToEmpty(properties.getResourcesFolder());
      default -> StringUtils.trimToEmpty(properties.getResourcesFolder());
    };
  }
}
