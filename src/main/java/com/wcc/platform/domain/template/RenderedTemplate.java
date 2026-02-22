package com.wcc.platform.domain.template;

/**
 * RenderedTemplate record representing a rendered email template.
 *
 * @param subject
 * @param body
 */
public record RenderedTemplate(String subject, String body) {
  public static RenderedTemplate from(final Template template) {
    return new RenderedTemplate(template.subject(), template.body());
  }
}
