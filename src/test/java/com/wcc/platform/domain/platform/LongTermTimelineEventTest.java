package com.wcc.platform.domain.platform;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class LongTermTimelineEventTest {
  private static Validator validator;

  @BeforeAll
  static void setupValidator() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  void builderAndGetters() {
    var event =
        LongTermTimelineEvent.builder()
            .duration("2020-2025")
            .title("Milestone")
            .description("Important description")
            .build();

    assertEquals("2020-2025", event.getDuration());
    assertEquals("Milestone", event.getTitle());
    assertEquals("Important description", event.getDescription());
  }

  @Test
  void equalsAndHashCode() {
    var event =
        LongTermTimelineEvent.builder()
            .duration("2020-2025")
            .title("Milestone")
            .description("Important description")
            .build();

    var event2 =
        LongTermTimelineEvent.builder()
            .duration("2020-2025")
            .title("Milestone")
            .description("Important description")
            .build();

    assertEquals(event, event2);
    assertEquals(event.hashCode(), event2.hashCode());
  }

  @Test
  void testToStringContainsFields() {
    var event =
        LongTermTimelineEvent.builder()
            .duration("2020-2025")
            .title("Milestone")
            .description("Important description")
            .build();

    var eventString = event.toString();
    assertTrue(eventString.contains("2020-2025"));
    assertTrue(eventString.contains("Milestone"));
    assertTrue(eventString.contains("Important description"));
  }

  @Test
  void validationPassesForValidInstance() {
    var event =
        LongTermTimelineEvent.builder()
            .duration("2020-2025")
            .title("Milestone")
            .description("Important description")
            .build();

    Set<ConstraintViolation<LongTermTimelineEvent>> violations = validator.validate(event);
    assertTrue(violations.isEmpty());
  }

  @Test
  void validationFailsForEmptyFields() {
    var event = LongTermTimelineEvent.builder().duration("").title("").description("").build();

    Set<ConstraintViolation<LongTermTimelineEvent>> violations = validator.validate(event);
    assertFalse(violations.isEmpty());

    var props =
        violations.stream().map(v -> v.getPropertyPath().toString()).collect(Collectors.toSet());
    assertTrue(props.contains("duration"));
    assertTrue(props.contains("title"));
    assertTrue(props.contains("description"));
  }

  @Test
  void validationFailsForNullFields() {
    var event = new LongTermTimelineEvent(); // no-args -> null fields
    Set<ConstraintViolation<LongTermTimelineEvent>> violations = validator.validate(event);
    assertFalse(violations.isEmpty());

    var props =
        violations.stream().map(v -> v.getPropertyPath().toString()).collect(Collectors.toSet());
    assertTrue(props.contains("duration"));
    assertTrue(props.contains("title"));
    assertTrue(props.contains("description"));
  }
}
