package com.wcc.platform.domain.exceptions;

/**
 * Exception thrown when a mentee tries to register with a priority already used in the same cycle.
 */
public class DuplicatedPriorityException extends DuplicatedException {
  public DuplicatedPriorityException(final String message) {
    super(message);
  }
}
