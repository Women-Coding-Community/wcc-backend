package com.wcc.platform.domain.exceptions;

/** Exception thrown when mentorship type doesn't match the current cycle's type. */
public class InvalidMentorshipTypeException extends RuntimeException {

  public InvalidMentorshipTypeException(final String message) {
    super(message);
  }
}
