package com.wcc.platform.domain.exceptions;

/** Platform generic exception. */
public class PlatformInternalException extends RuntimeException {

  public PlatformInternalException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
