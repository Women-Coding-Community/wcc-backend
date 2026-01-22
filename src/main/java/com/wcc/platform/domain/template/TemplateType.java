package com.wcc.platform.domain.template;

import lombok.Getter;

@Getter
public enum TemplateType {
  FEEDBACK_MENTOR_ADHOC("feedback_mentor_adhoc.yml"),
  FEEDBACK_MENTOR_LONG("feedback_mentor_long_term.yml"),
  NEW_CYCLE_MENTOR_LONG("joining_new_mentorship_cycle_mentor_long_term.yml"),
  WELCOME_MENTORSHIP_MENTEE_MENTOR_LONG("welcome_mentorship_mentee_mentor_long_term.yml"),
  KICK_OFF_MEETING_LONG("kick_off_meeting_mentor_mentee_long_term.yml"),
  MENTORSHIP_APPLICATION_RECEIVED_MENTEE_LONG("long_term_mentorship_application_received_mentee.yml"),
  UNABLE_MATCH_MENTOR_LONG("mentee_unable_match_with_mentor_long.yml"),
  REMINDER_LONG_MENTORSHIP_MENTEE("reminder_long_term_mentorship_mentee.yml"),
  POTENTIAL_MENTEES_MENTOR_LONG("list_potential_mentees_mentor_long.yml"),
  FOLLOW_UP_POTENTIAL_MENTEES_MENTOR_LONG("list_potential_mentees_follow_up_mentor_long.yml"),
  NEW_MENTEES_ALERT_MENTOR_LONG("alert_new_mentees_applications_mentor_long.yml"),
  STUDY_GROUP_AVAILABILITY_MENTOR("confirm_availability_study_group_mentor.yml"),
  STUDY_GROUPS_MENTEE("mentor_led_study_groups_mentee.yml"),
  STUDY_GROUP_INTRODUCTION_MENTEE("study_group_introduction_email_mentee.yml");


  private final String templateFile;

  TemplateType(final String templateFile) {
    this.templateFile = templateFile;
  }
}
