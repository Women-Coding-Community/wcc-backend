package com.wcc.platform.domain.exceptions;

/** Base exception for all duplicated record violations. */
public class DuplicatedException extends RuntimeException {
  public DuplicatedException(final String message) {
    super(message);
  }

  public DuplicatedException(final String message, final Throwable cause) {
    super(message, cause);
  }
}