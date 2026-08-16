package com.wcc.platform.domain.exceptions;

import lombok.extern.slf4j.Slf4j;

/** Platform Feedback not found exception. */
@Slf4j
public class FeedbackNotFoundException extends RuntimeException {

  public FeedbackNotFoundException(final Long feedbackId) {
    super("Feedback with id: " + feedbackId + " not found.");
  }

  public FeedbackNotFoundException(final String message) {
    super("Feedback not found: " + message);
  }
}
