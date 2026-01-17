package com.wcc.platform.domain.exceptions;

/** Exception thrown when a mentee exceeds the registration limit per cycle. */
public class MenteeRegistrationLimitExceededException extends RuntimeException {
  public MenteeRegistrationLimitExceededException(final String message) {
    super(message);
  }
}
