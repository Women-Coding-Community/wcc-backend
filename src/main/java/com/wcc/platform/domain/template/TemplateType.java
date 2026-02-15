package com.wcc.platform.domain.template;

import lombok.Getter;

@Getter
public enum TemplateType {
  FEEDBACK_MENTOR_ADHOC("feedback_mentor_adhoc.yml"),
  FEEDBACK_MENTOR_LONG("feedback_mentor_long_term.yml"),
  PROFILE_APPROVED_MENTOR("mentorship_profile_approved_mentor.yml");

  private final String templateFile;

  TemplateType(final String templateFile) {
    this.templateFile = templateFile;
  }
}
