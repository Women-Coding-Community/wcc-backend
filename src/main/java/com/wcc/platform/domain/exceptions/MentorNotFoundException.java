package com.wcc.platform.domain.exceptions;

/** Exception thrown when a mentor is not found in the database. */
public class MentorNotFoundException extends RuntimeException {
  public MentorNotFoundException(final Long mentorId) {
    super("Mentor not found: " + mentorId);
  }
}
