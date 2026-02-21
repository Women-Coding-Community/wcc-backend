package com.wcc.platform.domain.template;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * RenderedTemplate record representing a rendered email template.
 *
 * @param subject
 * @param body
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RenderedTemplate(String subject, String body) {
  public static RenderedTemplate from(final Template template) {
    return new RenderedTemplate(template.subject(), template.body());
  }
}
