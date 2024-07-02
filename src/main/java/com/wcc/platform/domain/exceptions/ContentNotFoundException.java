package com.wcc.platform.domain.exceptions;

/** CMS Content not found exception. */
public class ContentNotFoundException extends RuntimeException {

  public ContentNotFoundException(String message) {
    super(message);
  }
}
