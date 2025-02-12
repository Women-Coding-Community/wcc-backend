package com.wcc.platform.serializer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.wcc.platform.domain.platform.ProgramType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Integration test for StringToEnumConverter class.
 *
 * <p>StringToEnumConverter is used to convert string description to Enum ProgramType when sending
 * request for Program page.
 */
class StringToEnumConverterTest {
  private StringToEnumConverter converter;
  private ProgramType programType;

  @BeforeEach
  void setup() {
    converter = new StringToEnumConverter();
  }

  @Test
  void testConvertValidValue() {
    programType = converter.convert(ProgramType.BOOK_CLUB.toString());
    assertEquals(programType, ProgramType.BOOK_CLUB);
  }

  @Test
  void testConverterInvalidValueReturnOthers() {
    programType = converter.convert("Invalid value");
    assertEquals(programType, ProgramType.OTHERS);
  }

  @Test
  void testConverterNullValue() {
    programType = converter.convert(null);
    assertEquals(programType, ProgramType.OTHERS);
  }

  @Test
  void testConverterEmptyString() {
    programType = converter.convert("");
    assertEquals(programType, ProgramType.OTHERS);
  }
}
