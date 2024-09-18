package com.wcc.platform.domain.platform;

import static com.wcc.platform.factories.SetupEventFactories.createEventSectionTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wcc.platform.factories.SetupEventFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Test class for the class {@link EventSection}. */
class EventSectionTest {

  EventSection testEventSection;

  @BeforeEach
  void setup() {
    testEventSection = createEventSectionTest();
  }

  @Test
  void testEquals() {
    assertEquals(
        testEventSection, SetupEventFactories.createEventSectionTest(ProgramType.BOOK_CLUB));
  }

  @Test
  void testNotEquals() {
    assertNotEquals(
        testEventSection, SetupEventFactories.createEventSectionTest(ProgramType.TECH_TALK));
  }

  @Test
  void testHashCode() {
    assertEquals(
        testEventSection.hashCode(),
        SetupEventFactories.createEventSectionTest(ProgramType.BOOK_CLUB).hashCode());
  }

  @Test
  void testHashCodeNotEquals() {
    assertNotEquals(
        testEventSection.hashCode(),
        SetupEventFactories.createEventSectionTest(ProgramType.TECH_TALK).hashCode());
  }

  @Test
  void testToString() {
    assertTrue(testEventSection.toString().contains(ProgramType.BOOK_CLUB.toString()));
  }
}
