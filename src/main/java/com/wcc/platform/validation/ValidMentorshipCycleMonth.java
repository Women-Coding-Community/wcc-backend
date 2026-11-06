package com.wcc.platform.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;

/**
 * Validates that a mentorship cycle month is between February and November.
 */
@Documented
@Constraint(validatedBy = MentorshipCycleMonthValidator.class)
@Target({
  ElementType.FIELD,
  ElementType.PARAMETER,
  ElementType.ANNOTATION_TYPE,
  ElementType.RECORD_COMPONENT
})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidMentorshipCycleMonth {

  String message() default "Cycle month must be between February and November";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
