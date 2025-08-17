package com.wcc.platform.domain.resource;

/** Enum representing the different types of resources that can be stored. */
public enum ResourceType {
  PROFILE_PICTURE(1),
  IMAGE(2),
  PDF(3),
  PRESENTATION(4),
  OTHER(5);

  private final int resourceTypeId;

  ResourceType(int resourceTypeId) {
    this.resourceTypeId = resourceTypeId;
  }

  public int getResourceTypeId() {
    return resourceTypeId;
  }
}
