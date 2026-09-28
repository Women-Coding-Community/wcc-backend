package com.wcc.platform.domain.exceptions;

/** Exception thrown when a mentorship cycle is not found. */
public class CycleNotFoundException extends RuntimeException {

  public CycleNotFoundException(final Long cycleId) {
    super("Mentorship cycle not found with ID: " + cycleId);
  }

  public CycleNotFoundException(final String message) {
    super(message);
  }
}
