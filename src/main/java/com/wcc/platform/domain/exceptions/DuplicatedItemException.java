package com.wcc.platform.domain.exceptions;

/** Repository duplicated exception. */
public class DuplicatedItemException extends RuntimeException {
  public DuplicatedItemException(final String message) {
    super(message);
  }
}
