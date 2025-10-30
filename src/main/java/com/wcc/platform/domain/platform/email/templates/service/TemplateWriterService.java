package com.wcc.platform.domain.platform.email.templates.service;

import com.wcc.platform.domain.platform.email.templates.RenderedTemplate;
import com.wcc.platform.domain.platform.email.templates.TemplateRequest;

public interface TemplateWriterService {
  /**
   * Render a template identified by the TemplateRequest into a subject and HTML body.
   *
   * @param request contains templateType, mentorshipType and params for placeholders
   * @return rendered subject and HTML body
   * @throws IllegalArgumentException if request or required template is missing
   */
  RenderedTemplate render(final TemplateRequest request);
}
