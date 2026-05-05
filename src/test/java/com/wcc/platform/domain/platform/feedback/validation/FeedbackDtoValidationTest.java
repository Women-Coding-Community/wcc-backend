package com.wcc.platform.domain.platform.feedback.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wcc.platform.domain.platform.feedback.FeedbackDto;
import com.wcc.platform.domain.platform.type.FeedbackType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Test class for FeedbackDto validation. */
class FeedbackDtoValidationTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @DisplayName("Given valid mentor review feedback DTO, when validating, then validation passes")
  void testValidMentorReviewFeedback() {
    FeedbackDto dto =
        FeedbackDto.builder()
            .reviewerId(1L)
            .revieweeId(2L)
            .mentorshipCycleId(1L)
            .feedbackType(FeedbackType.MENTOR_REVIEW)
            .rating(5)
            .feedbackText("Great mentor")
            .isAnonymous(false)
            .build();

    Set<ConstraintViolation<FeedbackDto>> violations = validator.validate(dto);
    assertTrue(violations.isEmpty(), "Valid MENTOR_REVIEW should have no violations");
  }

  @Test
  @DisplayName("Given mentor review without reviewee ID, when validating, then validation fails")
  void testMentorReviewWithoutRevieweeIdFails() {
    FeedbackDto dto =
        FeedbackDto.builder()
            .reviewerId(1L)
            .mentorshipCycleId(1L)
            .feedbackType(FeedbackType.MENTOR_REVIEW)
            .rating(5)
            .feedbackText("Great mentor")
            .isAnonymous(false)
            .build();

    Set<ConstraintViolation<FeedbackDto>> violations = validator.validate(dto);
    assertFalse(violations.isEmpty(), "MENTOR_REVIEW without revieweeId should fail");
    assertEquals(1, violations.size());
    assertTrue(
        violations.stream()
            .anyMatch(v -> v.getMessage().contains("revieweeId is required for MENTOR_REVIEW")));
  }

  @Test
  @DisplayName(
      "Given mentor review without mentorship cycle ID, when validating, then validation fails")
  void testMentorReviewWithoutMentorshipCycleIdFails() {
    FeedbackDto dto =
        FeedbackDto.builder()
            .reviewerId(1L)
            .revieweeId(2L)
            .feedbackType(FeedbackType.MENTOR_REVIEW)
            .rating(5)
            .feedbackText("Great mentor")
            .isAnonymous(false)
            .build();

    Set<ConstraintViolation<FeedbackDto>> violations = validator.validate(dto);
    assertFalse(violations.isEmpty(), "MENTOR_REVIEW without mentorshipCycleId should fail");
    assertEquals(1, violations.size());
    assertTrue(
        violations.stream()
            .anyMatch(v -> v.getMessage().contains("mentorshipCycleId is required")));
  }

  @Test
  @DisplayName(
      "Given valid mentorship program feedback DTO, when validating, then validation passes")
  void testValidMentorshipProgramFeedback() {
    FeedbackDto dto =
        FeedbackDto.builder()
            .reviewerId(1L)
            .mentorshipCycleId(1L)
            .feedbackType(FeedbackType.MENTORSHIP_PROGRAM)
            .rating(4)
            .feedbackText("Great program")
            .isAnonymous(true)
            .build();

    Set<ConstraintViolation<FeedbackDto>> violations = validator.validate(dto);
    assertTrue(violations.isEmpty(), "Valid MENTORSHIP_PROGRAM should have no violations");
  }

  @Test
  @DisplayName("Given mentorship program without cycle ID, when validating, then validation fails")
  void testMentorshipProgramWithoutCycleIdFails() {
    FeedbackDto dto =
        FeedbackDto.builder()
            .reviewerId(1L)
            .feedbackType(FeedbackType.MENTORSHIP_PROGRAM)
            .rating(4)
            .feedbackText("Great program")
            .isAnonymous(true)
            .build();

    Set<ConstraintViolation<FeedbackDto>> violations = validator.validate(dto);
    assertFalse(violations.isEmpty(), "MENTORSHIP_PROGRAM without mentorshipCycleId should fail");
    assertEquals(1, violations.size());
    assertTrue(
        violations.stream()
            .anyMatch(
                v ->
                    v.getMessage()
                        .contains("mentorshipCycleId is required for MENTORSHIP_PROGRAM")));
  }

  @Test
  @DisplayName("Given mentor review without rating, when validating, then validation fails")
  void testMentorReviewWithoutRatingFails() {
    FeedbackDto dto =
        FeedbackDto.builder()
            .reviewerId(1L)
            .revieweeId(2L)
            .mentorshipCycleId(1L)
            .feedbackType(FeedbackType.MENTOR_REVIEW)
            .feedbackText("Great mentor")
            .isAnonymous(false)
            .build();

    Set<ConstraintViolation<FeedbackDto>> violations = validator.validate(dto);
    assertFalse(violations.isEmpty(), "MENTOR_REVIEW without rating should fail");
    assertEquals(1, violations.size());
    assertTrue(
        violations.stream()
            .anyMatch(v -> v.getMessage().contains("rating is required for MENTOR_REVIEW")));
  }

  @Test
  @DisplayName("Given mentorship program without rating, when validating, then validation fails")
  void testMentorshipProgramWithoutRatingFails() {
    FeedbackDto dto =
        FeedbackDto.builder()
            .reviewerId(1L)
            .mentorshipCycleId(1L)
            .feedbackType(FeedbackType.MENTORSHIP_PROGRAM)
            .feedbackText("Great program")
            .isAnonymous(true)
            .build();

    Set<ConstraintViolation<FeedbackDto>> violations = validator.validate(dto);
    assertFalse(violations.isEmpty(), "MENTORSHIP_PROGRAM without rating should fail");
    assertEquals(1, violations.size());
    assertTrue(
        violations.stream()
            .anyMatch(v -> v.getMessage().contains("rating is required for MENTORSHIP_PROGRAM")));
  }

  @Test
  @DisplayName(
      "Given valid community general feedback DTO, when validating, then validation passes")
  void testValidCommunityGeneralFeedback() {
    FeedbackDto dto =
        FeedbackDto.builder()
            .reviewerId(1L)
            .feedbackType(FeedbackType.COMMUNITY_GENERAL)
            .rating(5)
            .feedbackText("Amazing community")
            .isAnonymous(false)
            .build();

    Set<ConstraintViolation<FeedbackDto>> violations = validator.validate(dto);
    assertTrue(violations.isEmpty(), "Valid COMMUNITY_GENERAL should have no violations");
  }

  @Test
  @DisplayName(
      "Given community general feedback without rating, when validating, then validation passes")
  void testValidCommunityGeneralFeedbackWithoutRating() {
    FeedbackDto dto =
        FeedbackDto.builder()
            .reviewerId(1L)
            .feedbackType(FeedbackType.COMMUNITY_GENERAL)
            .feedbackText("Amazing community")
            .isAnonymous(false)
            .build();

    Set<ConstraintViolation<FeedbackDto>> violations = validator.validate(dto);
    assertTrue(
        violations.isEmpty(),
        "COMMUNITY_GENERAL without rating should be valid (rating is optional)");
  }

  @Test
  @DisplayName("Given DTO with missing required fields, when validating, then validation fails")
  void testMissingRequiredFieldsFails() {
    FeedbackDto dto = FeedbackDto.builder().build();

    Set<ConstraintViolation<FeedbackDto>> violations = validator.validate(dto);
    assertFalse(violations.isEmpty(), "DTO without required fields should fail");
    // Should have violations for: reviewerId, feedbackType, feedbackText, isAnonymous
    assertTrue(violations.size() >= 4);
  }

  @Test
  @DisplayName("Given DTO with invalid rating value, when validating, then validation fails")
  void testInvalidRatingFails() {
    FeedbackDto dto =
        FeedbackDto.builder()
            .reviewerId(1L)
            .feedbackType(FeedbackType.COMMUNITY_GENERAL)
            .rating(6) // Invalid: must be 1-5
            .feedbackText("Great!")
            .isAnonymous(true)
            .build();

    Set<ConstraintViolation<FeedbackDto>> violations = validator.validate(dto);
    assertFalse(violations.isEmpty(), "Rating > 5 should fail");
    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("rating")));
  }

  @Test
  @DisplayName("Given DTO with blank feedback text, when validating, then validation fails")
  void testBlankFeedbackTextFails() {
    FeedbackDto dto =
        FeedbackDto.builder()
            .reviewerId(1L)
            .feedbackType(FeedbackType.COMMUNITY_GENERAL)
            .rating(5)
            .feedbackText("   ") // Blank
            .isAnonymous(true)
            .build();

    Set<ConstraintViolation<FeedbackDto>> violations = validator.validate(dto);
    assertFalse(violations.isEmpty(), "Blank feedback text should fail");
    assertTrue(
        violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("feedbackText")));
  }

  @Test
  @DisplayName("Given DTO with year below 2000, when validating, then validation fails")
  void testYearBelowMinFails() {
    FeedbackDto dto =
        FeedbackDto.builder()
            .reviewerId(1L)
            .feedbackType(FeedbackType.COMMUNITY_GENERAL)
            .feedbackText("Great!")
            .isAnonymous(false)
            .year(1999)
            .build();

    Set<ConstraintViolation<FeedbackDto>> violations = validator.validate(dto);
    assertFalse(violations.isEmpty(), "Year below 2000 should fail");
    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("year")));
  }

  @Test
  @DisplayName("Given DTO with year above 2100, when validating, then validation fails")
  void testYearAboveMaxFails() {
    FeedbackDto dto =
        FeedbackDto.builder()
            .reviewerId(1L)
            .feedbackType(FeedbackType.COMMUNITY_GENERAL)
            .feedbackText("Great!")
            .isAnonymous(false)
            .year(2101)
            .build();

    Set<ConstraintViolation<FeedbackDto>> violations = validator.validate(dto);
    assertFalse(violations.isEmpty(), "Year above 2100 should fail");
    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("year")));
  }

  @Test
  @DisplayName("Given DTO with valid year boundary values, when validating, then validation passes")
  void testYearBoundaryValuesPass() {
    FeedbackDto dtoMin =
        FeedbackDto.builder()
            .reviewerId(1L)
            .feedbackType(FeedbackType.COMMUNITY_GENERAL)
            .feedbackText("Great!")
            .isAnonymous(false)
            .year(2000)
            .build();

    FeedbackDto dtoMax =
        FeedbackDto.builder()
            .reviewerId(1L)
            .feedbackType(FeedbackType.COMMUNITY_GENERAL)
            .feedbackText("Great!")
            .isAnonymous(false)
            .year(2100)
            .build();

    assertTrue(validator.validate(dtoMin).isEmpty(), "Year 2000 (min boundary) should be valid");
    assertTrue(validator.validate(dtoMax).isEmpty(), "Year 2100 (max boundary) should be valid");
  }
}
