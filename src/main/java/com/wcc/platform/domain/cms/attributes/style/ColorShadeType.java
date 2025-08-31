package com.wcc.platform.domain.cms.attributes.style;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Colour Shade types to be used in the frontend based on MUI UI. */
@Getter
@AllArgsConstructor
public enum ColorShadeType {
  MAIN("main", 40),
  LIGHT("light", 90),
  DARK("dark", 10);

  private final String description;
  private final int shade;

  @Override
  public String toString() {
    return description;
  }
}
