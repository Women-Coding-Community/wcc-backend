package com.wcc.platform.domain.exceptions;

public class ForbiddenException extends RuntimeException {

  public ForbiddenException(final String message) {
    super(message);
  }
}
