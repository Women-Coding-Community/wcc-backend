package com.wcc.platform.domain.exceptions;

/** When mentee cannot be saved exception. */
public class MenteeNotSavedException extends RuntimeException {
  public MenteeNotSavedException(final String message) {
    super(message);
  }
}
