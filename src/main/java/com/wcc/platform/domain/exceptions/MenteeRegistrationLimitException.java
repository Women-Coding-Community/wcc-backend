package com.wcc.platform.domain.exceptions;

/** Exception thrown when a mentee exceeds the registration limit per cycle. */
public class MenteeRegistrationLimitException extends RuntimeException {
  public MenteeRegistrationLimitException(final String message) {
    super(message);
  }
}
