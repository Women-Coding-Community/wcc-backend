package com.wcc.platform.domain.exceptions;

import com.wcc.platform.domain.cms.PageType;
import com.wcc.platform.domain.platform.ProgramType;

/** Platform generic exception. */
public class PlatformInternalException extends RuntimeException {
  public PlatformInternalException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public PlatformInternalException(final PageType pageType, final Throwable cause) {
    super("Invalid Page type " + pageType, cause);
  }

  public PlatformInternalException(final ProgramType programType, final Throwable cause) {
    super("Invalid Page type to program" + programType.toPageId(), cause);
  }
}
