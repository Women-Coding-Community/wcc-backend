package com.wcc.platform.domain.template;

import lombok.Data;

@Data
public class RenderedTemplate {
  private String subject;
  private String body;

  public static RenderedTemplate from(Template template) {
    RenderedTemplate rendered = new RenderedTemplate();
    rendered.setSubject(template.getSubject());
    rendered.setBody(template.getBody());
    return rendered;
  }
}
