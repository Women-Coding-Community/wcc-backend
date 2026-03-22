package com.wcc.platform.domain.platform.feedback.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Custom validation annotation for FeedbackDto. */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FeedbackValidator.class)
public @interface ValidFeedback {
  String message() default "Invalid feedback data";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
