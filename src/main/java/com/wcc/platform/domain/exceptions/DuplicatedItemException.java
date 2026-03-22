package com.wcc.platform.domain.exceptions;

/** Repository duplicated exception. */
public class DuplicatedItemException extends DuplicatedException {
  public DuplicatedItemException(final String message) {
    super(message);
  }

  public DuplicatedItemException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
