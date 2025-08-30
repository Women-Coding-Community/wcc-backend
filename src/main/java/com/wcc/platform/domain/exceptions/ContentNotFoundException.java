package com.wcc.platform.domain.exceptions;

import com.wcc.platform.domain.cms.PageType;
import com.wcc.platform.domain.platform.type.ProgramType;
import lombok.extern.slf4j.Slf4j;

/** CMS Content not found exception. */
@Slf4j
public class ContentNotFoundException extends RuntimeException {

  private static final String CONTENT_OF_PAGE = "Content of Page ";
  private static final String NOT_FOUND = " not found";

  public ContentNotFoundException(final String message) {
    super(message);
  }

  public ContentNotFoundException(final PageType pageType) {
    super(CONTENT_OF_PAGE + pageType + NOT_FOUND);
  }

  public ContentNotFoundException(final PageType pageType, final Exception exception) {
    super(CONTENT_OF_PAGE + pageType + NOT_FOUND);
    log.error(exception.getMessage(), exception);
  }

  public ContentNotFoundException(final ProgramType programType) {
    super(CONTENT_OF_PAGE + programType.toPageId() + NOT_FOUND);
  }
}
