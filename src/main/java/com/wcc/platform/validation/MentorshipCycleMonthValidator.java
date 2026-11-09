package com.wcc.platform.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.Month;

/** Validates the allowed month range for mentorship cycles. */
public class MentorshipCycleMonthValidator
    implements ConstraintValidator<ValidMentorshipCycleMonth, Month> {

  @Override
  public boolean isValid(final Month month, final ConstraintValidatorContext context) {
    return month == null || (month.getValue() >= 2 && month.getValue() <= 11);
  }
}
