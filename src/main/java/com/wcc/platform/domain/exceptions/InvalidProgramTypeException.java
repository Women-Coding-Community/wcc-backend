package com.wcc.platform.domain.exceptions;

/** Platform InvalidProgramType exception. */
public class InvalidProgramTypeException extends RuntimeException {

  public InvalidProgramTypeException(final String message) {
    super(message);
  }
}
