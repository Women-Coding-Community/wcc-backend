package com.wcc.platform.domain.platform.feedback.validation;

import com.wcc.platform.domain.platform.feedback.FeedbackDto;
import com.wcc.platform.domain.platform.type.FeedbackType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/** Validator for FeedbackDto that checks conditional field requirements. */
public class FeedbackValidator implements ConstraintValidator<ValidFeedback, FeedbackDto> {

  @Override
  public boolean isValid(final FeedbackDto dto, final ConstraintValidatorContext context) {
    if (dto == null) {
      return true; // Let @NotNull handle null checks
    }

    context.disableDefaultConstraintViolation();

    boolean isValid = true;

    // Validate mentorshipCycleId for MENTORSHIP_PROGRAM and MENTOR_REVIEW
    if ((dto.getFeedbackType() == FeedbackType.MENTORSHIP_PROGRAM
            || dto.getFeedbackType() == FeedbackType.MENTOR_REVIEW)
        && dto.getMentorshipCycleId() == null) {
      context
          .buildConstraintViolationWithTemplate(
              "mentorshipCycleId is required for " + dto.getFeedbackType() + " feedback")
          .addPropertyNode("mentorshipCycleId")
          .addConstraintViolation();
      isValid = false;
    }

    // Validate revieweeId for MENTOR_REVIEW
    if (dto.getFeedbackType() == FeedbackType.MENTOR_REVIEW && dto.getRevieweeId() == null) {
      context
          .buildConstraintViolationWithTemplate("revieweeId is required for MENTOR_REVIEW feedback")
          .addPropertyNode("revieweeId")
          .addConstraintViolation();
      isValid = false;
    }

    // Validate rating for MENTOR_REVIEW and MENTORSHIP_PROGRAM
    if ((dto.getFeedbackType() == FeedbackType.MENTOR_REVIEW
            || dto.getFeedbackType() == FeedbackType.MENTORSHIP_PROGRAM)
        && dto.getRating() == null) {
      context
          .buildConstraintViolationWithTemplate(
              "rating is required for " + dto.getFeedbackType() + " feedback")
          .addPropertyNode("rating")
          .addConstraintViolation();
      isValid = false;
    }

    return isValid;
  }
}
