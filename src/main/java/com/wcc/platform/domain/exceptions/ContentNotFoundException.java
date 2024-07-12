package com.wcc.platform.domain.exceptions;

/** CMS Content not found exception. */
public class ContentNotFoundException extends RuntimeException {

  public ContentNotFoundException(final String message) {
    super(message);
  }
}
