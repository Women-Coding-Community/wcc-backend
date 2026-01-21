package com.wcc.platform.domain.template;

import lombok.Getter;

@Getter
public enum TemplateType {
  FEEDBACK_MENTOR_ADHOC("feedback_mentor_adhoc.yml"),
  FEEDBACK_MENTOR_LONG("feedback_mentor_long_term.yml"),
  NEW_CYCLE_MENTOR_LONG("joining_new_mentorship_cycle_mentor_long_term.yml"),
  WELCOME_MENTORSHIP_MENTEE_MENTOR_LONG("welcome_mentorship_mentee_mentor_long_term.yml");

  private final String templateFile;

  TemplateType(final String templateFile) {
    this.templateFile = templateFile;
  }
}
