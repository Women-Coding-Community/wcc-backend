package com.wcc.platform.domain.platform;

import static com.wcc.platform.factories.SetupEventFactories.createEventTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Test class for the class {@link Event}. */
class EventTest {

  Event testEvent;
  Validator validator;

  @BeforeEach
  void setup() {testEvent = createEventTest();}

  @Test
  void testEqual() {
    assertEquals(testEvent, createEventTest(ProgramType.BOOK_CLUB));
  }

  @Test
  void testNotEqual() {
    assertNotEquals(testEvent, createEventTest(ProgramType.TECH_TALK));
    assertNotEquals(testEvent, createEventTest(ProgramType.WRITING_CLUB));
    assertNotEquals(
        createEventTest(ProgramType.WRITING_CLUB), createEventTest(ProgramType.TECH_TALK));
  }

  @Test
  void testHashCode() {
    assertEquals(testEvent.hashCode(), createEventTest(ProgramType.BOOK_CLUB).hashCode());
  }

  @Test
  void testHashCodeNotEquals() {
    assertNotEquals(testEvent.hashCode(), createEventTest(ProgramType.WRITING_CLUB).hashCode());
  }

  @Test
  void testToString() {
    assertTrue(testEvent.toString().contains(ProgramType.BOOK_CLUB.toString()));
    assertTrue(testEvent.toString().contains(ProgramType.BOOK_CLUB.toString()));
  }

  @Test
  void testForFieldValidation(){
    try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
      validator = factory.getValidator();
    }
    Set<ConstraintViolation<Event>> violations = validator.validate(testEvent);
    assertFalse(violations.isEmpty());
    assertEquals(3, violations.size());
    assertTrue(
        violations.stream()
            .anyMatch(
                v ->
                    v.getPropertyPath().toString().equals("eventType")
                        && v.getMessage().equals("must not be null")));
    assertTrue(
        violations.stream()
            .anyMatch(
                v ->
                    v.getPropertyPath().toString().equals("endDate")
                        && v.getMessage().equals("must not be null")));

    assertTrue(
        violations.stream()
            .anyMatch(
                v ->
                    v.getPropertyPath().toString().equals("images")
                        && v.getMessage().equals("must not be empty")));
  }
}
