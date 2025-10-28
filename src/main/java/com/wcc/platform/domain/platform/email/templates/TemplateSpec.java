package com.wcc.platform.domain.platform.email.templates;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateSpec {
  private String subject;
  private String body;
}
