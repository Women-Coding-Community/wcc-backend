package com.wcc.platform.domain.exceptions;

import com.wcc.platform.domain.cms.PageType;
import com.wcc.platform.domain.platform.ProgramType;
import lombok.extern.slf4j.Slf4j;

/** CMS Content not found exception. */
@Slf4j
public class ContentNotFoundException extends RuntimeException {

  public ContentNotFoundException(final String message) {
    super(message);
  }

  public ContentNotFoundException(final PageType pageType) {
    super("Content of Page " + pageType + " not found");
  }

  public ContentNotFoundException(final PageType pageType, final Exception exception) {
    super("Content of Page " + pageType + " not found");
    log.error(exception.getMessage(), exception);
  }

  public ContentNotFoundException(final ProgramType programType) {
    super("Content of Page " + programType.toPageId() + " not found");
  }
}
