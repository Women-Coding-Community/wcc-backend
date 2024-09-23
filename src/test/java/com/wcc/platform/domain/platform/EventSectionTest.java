package com.wcc.platform.domain.platform;

import static com.wcc.platform.factories.SetupEventFactories.createEventSection;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Test class for the class {@link EventSection}. */
class EventSectionTest {

  EventSection testEventSection;

  @BeforeEach
  void setup() {
    testEventSection = createEventSection();
  }

  @Test
  void testEquals() {
    assertEquals(testEventSection, createEventSection(ProgramType.BOOK_CLUB));
  }

  @Test
  void testNotEquals() {
    assertNotEquals(testEventSection, createEventSection(ProgramType.TECH_TALK));
  }

  @Test
  void testHashCode() {
    assertEquals(testEventSection.hashCode(), createEventSection(ProgramType.BOOK_CLUB).hashCode());
  }

  @Test
  void testHashCodeNotEquals() {
    assertNotEquals(
        testEventSection.hashCode(), createEventSection(ProgramType.TECH_TALK).hashCode());
  }

  @Test
  void testToString() {
    assertTrue(testEventSection.toString().contains(ProgramType.BOOK_CLUB.toString()));
  }
}
