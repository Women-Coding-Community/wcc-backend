package com.wcc.platform.domain.exceptions;

/** Thrown when an operation cannot be performed due to the mentor's current status. */
public class MentorStatusException extends RuntimeException {

  /**
   * Create exception with the given message.
   *
   * @param message description of the status conflict
   */
  public MentorStatusException(final String message) {
    super(message);
  }
}
