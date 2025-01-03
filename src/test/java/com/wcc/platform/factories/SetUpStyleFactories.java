package com.wcc.platform.factories;

import com.wcc.platform.domain.cms.attributes.style.BackgroundColourStyle;
import com.wcc.platform.domain.cms.attributes.style.ColorShadeType;
import com.wcc.platform.domain.cms.attributes.style.ColorType;
import com.wcc.platform.domain.cms.attributes.style.CustomStyle;

/** Style set-up factories. */
public class SetUpStyleFactories {

  /** Create the filters object. */
  public static CustomStyle createCustomStyleTest() {
    return new CustomStyle(
        new BackgroundColourStyle(ColorType.PRIMARY, ColorShadeType.LIGHT.toColorShade()));
  }

  public static CustomStyle backgroundSecondary() {
    return new CustomStyle(
        new BackgroundColourStyle(ColorType.SECONDARY, ColorShadeType.DARK.toColorShade()));
  }
}
