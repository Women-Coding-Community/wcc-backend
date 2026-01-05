package com.wcc.platform.domain.exceptions;

/**
 * Exception thrown when a template fails validation. This runtime exception indicates a template
 * did not pass validation rules.
 */
public class TemplateValidationException extends RuntimeException {
  /**
   * Create a new TemplateValidationException with the given detail message.
   *
   * @param message description of the validation error
   */
  public TemplateValidationException(final String message) {
    super(message);
  }
}
