package com.wcc.platform.domain.cms.attributes.style;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class ColorTypeTest {

  @ParameterizedTest
  @EnumSource(ColorType.class)
  void testToString(final ColorType colorType) {
    final String expected = colorType.name().toLowerCase(Locale.ENGLISH);
    assertEquals(expected, colorType.toString());
  }
}
