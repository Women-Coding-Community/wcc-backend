package com.wcc.platform.domain.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Enum representing the different types of resources that can be stored. */
@Getter
@AllArgsConstructor
public enum ResourceType {
  PROFILE_PICTURE(1),
  IMAGE(2),
  PDF(3),
  PRESENTATION(4),
  OTHER(5);

  private final int resourceTypeId;
}
