package com.wcc.platform.domain.cms.attributes.style;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class ColorShadeTypeTest {

  @ParameterizedTest
  @EnumSource(ColorShadeType.class)
  void testToString(ColorShadeType shadeType) {
    String expected = shadeType.name().toLowerCase(Locale.ENGLISH);
    assertEquals(expected, shadeType.toString());
  }
}
