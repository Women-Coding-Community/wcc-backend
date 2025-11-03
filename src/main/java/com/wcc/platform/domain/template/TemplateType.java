package com.wcc.platform.domain.template;

import lombok.Getter;

@Getter
public enum TemplateType {
  FEEDBACK_FROM_MENTOR_ADHOC("feedback_mentor_adhoc.yml"),
  FEEDBACK_FROM_MENTOR_LONG_TERM("feedback_mentor_long_term.yml");

  private final String templateFile;

  TemplateType(String templateFile) {
    this.templateFile = templateFile;
  }
}
