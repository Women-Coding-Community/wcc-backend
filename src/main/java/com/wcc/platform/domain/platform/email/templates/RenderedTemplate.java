package com.wcc.platform.domain.platform.email.templates;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RenderedTemplate {
  String subject;
  String bodyHtml;
}
