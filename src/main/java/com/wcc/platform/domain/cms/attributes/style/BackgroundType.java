package com.wcc.platform.domain.cms.attributes.style;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Background colour options to be used in the frontend. */
@AllArgsConstructor
@Getter
public enum BackgroundType {
  PRIMARY(ColorType.PRIMARY, ColorShadeType.LIGHT),
  SECONDARY(ColorType.SECONDARY, ColorShadeType.LIGHT),
  TERTIARY(ColorType.TERTIARY, ColorShadeType.LIGHT);

  private final ColorType color;
  private final ColorShadeType shade;
}
