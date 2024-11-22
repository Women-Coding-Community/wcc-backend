package com.wcc.platform.domain.exceptions;

import com.wcc.platform.domain.cms.PageType;

/** CMS Content not found exception. */
public class ContentNotFoundException extends RuntimeException {

  public ContentNotFoundException(final String message) {
    super(message);
  }

  public ContentNotFoundException(final PageType pageType) {
    super("Content of Page " + pageType + " not found");
  }
}
