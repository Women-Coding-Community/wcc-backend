package com.wcc.platform.domain.cms.attributes.style;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** Custom color to be used in the frontend based on MUI UI. */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class ColorShade {
  private ColorShadeType name;
  private int value;
}
