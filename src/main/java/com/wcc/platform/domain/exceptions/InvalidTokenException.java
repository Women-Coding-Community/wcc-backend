package com.wcc.platform.domain.exceptions;

/** Thrown when a supplied token is not found, already used, or has expired. */
public class InvalidTokenException extends RuntimeException {

  public InvalidTokenException(final String message) {
    super(message);
  }
}
