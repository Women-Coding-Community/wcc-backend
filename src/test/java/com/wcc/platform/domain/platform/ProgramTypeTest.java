package com.wcc.platform.domain.platform;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/** Test class for {@link ProgramType}. */
class ProgramTypeTest {

  @ParameterizedTest
  @EnumSource(ProgramType.class)
  void testToString(final ProgramType programType) {
    final String expected = programType.getDescription();
    assertEquals(expected, programType.toString());
  }
}
