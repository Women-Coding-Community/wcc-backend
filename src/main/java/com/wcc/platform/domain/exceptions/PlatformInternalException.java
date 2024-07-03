package com.wcc.platform.domain.exceptions;

/** Platform generic exception. */
public class PlatformInternalException extends RuntimeException {

  public PlatformInternalException(String message, Throwable cause) {
    super(message, cause);
  }
}
