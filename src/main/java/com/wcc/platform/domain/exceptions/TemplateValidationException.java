package com.wcc.platform.domain.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TemplateValidationException extends RuntimeException {
  public TemplateValidationException(final String message) {
    super(message);
  }
}
