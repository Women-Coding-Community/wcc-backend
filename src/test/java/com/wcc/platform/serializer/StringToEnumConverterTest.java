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
    assertEquals(ProgramType.BOOK_CLUB, programType);
  }

  @Test
  void testConverterInvalidValueReturnOthers() {
    programType = converter.convert("Invalid value");
    assertEquals(ProgramType.OTHERS, programType);
  }

  @Test
  void testConverterEmptyString() {
    programType = converter.convert("");
    assertEquals(ProgramType.OTHERS, programType);
  }
}
