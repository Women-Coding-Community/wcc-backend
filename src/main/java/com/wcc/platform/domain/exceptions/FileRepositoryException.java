package com.wcc.platform.domain.exceptions;

/** File Repository generic exception. */
public class FileRepositoryException extends RuntimeException {

  public FileRepositoryException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
