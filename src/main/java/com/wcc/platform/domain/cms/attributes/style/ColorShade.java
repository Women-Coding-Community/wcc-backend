package com.wcc.platform.domain.cms.attributes.style;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Custom color to be used in the frontend based on MUI UI. */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ColorShade {
  private ColorShadeType name;
  private int value;
}
