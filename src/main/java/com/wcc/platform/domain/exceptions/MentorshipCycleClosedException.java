package com.wcc.platform.domain.exceptions;

/** Exception thrown when attempting to register for a closed mentorship cycle. */
public class MentorshipCycleClosedException extends RuntimeException {

  public MentorshipCycleClosedException(final String message) {
    super(message);
  }
}
