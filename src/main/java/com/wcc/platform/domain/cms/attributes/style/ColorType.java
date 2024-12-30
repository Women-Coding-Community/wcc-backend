package com.wcc.platform.domain.cms.attributes.style;

import java.util.Locale;

/** Colour types to be used in the frontend based on MUI UI. */
public enum ColorType {
  PRIMARY,
  SECONDARY,
  TERTIARY;

  @Override
  public String toString() {
    return name().toLowerCase(Locale.ENGLISH);
  }
}
