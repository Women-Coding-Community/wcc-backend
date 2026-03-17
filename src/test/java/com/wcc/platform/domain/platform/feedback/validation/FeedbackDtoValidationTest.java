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
  void testMissingRequiredFieldsFails() {
    FeedbackDto dto = FeedbackDto.builder().build();

    Set<ConstraintViolation<FeedbackDto>> violations = validator.validate(dto);
    assertFalse(violations.isEmpty(), "DTO without required fields should fail");
    // Should have violations for: reviewerId, feedbackType, feedbackText, isAnonymous
    assertTrue(violations.size() >= 4);
  }

  @Test
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
}
