package com.wcc.platform.domain.template;

import lombok.Getter;

/**
 * Enum representing different types of email templates. Each enum constant corresponds to a
 * specific template file used for generating email content.
 */
@Getter
@SuppressWarnings("PMD.LongVariable")
public enum TemplateType {
  FEEDBACK_MENTOR_ADHOC("feedback_mentor_adhoc.yml"),
  FEEDBACK_MENTOR_LONG("feedback_mentor_long_term.yml"),
  NEW_CYCLE_MENTOR_LONG("joining_new_mentorship_cycle_mentor_long_term.yml"),
  MATCH_APPLICATIONS("match_applications.yml"),
  MENTEE_APPLICATIONS("mentee_applications.yml"),
  WELCOME_LONG("welcome_mentorship_mentee_mentor_long_term.yml"),
  KICK_OFF_MEETING_LONG("kick_off_meeting_mentor_mentee_long_term.yml"),
  MENTEE_APP_LONG("long_term_mentorship_application_received_mentee.yml"),
  NO_MATCH_MENTOR_LONG("mentee_unable_match_with_mentor_long.yml"),
  MENTEE_LONG_REJECTED_BY_ADMIN("mentee_long_term_rejected_by_admin.yml"),
  REMINDER_MENTEE_LONG("reminder_long_term_mentorship_mentee.yml"),
  MENTEES_MENTOR_LONG("list_potential_mentees_mentor_long.yml"),
  FOLLOWUP_MENTEES_LONG("list_potential_mentees_follow_up_mentor_long.yml"),
  NEW_MENTEES_LONG("alert_new_mentees_applications_mentor_long.yml"),
  STUDY_GROUP_AVAIL("confirm_availability_study_group_mentor.yml"),
  STUDY_GROUPS_MENTEE("mentor_led_study_groups_mentee.yml"),
  STUDY_GROUP_INTRO("study_group_introduction_email_mentee.yml"),
  NOT_ON_SLACK_MENTOR("not_on_slack_mentor_applicant.yml"),
  MENTOR_PROFILE_REJECT("mentorship_profile_not_approved_mentor.yml"),
  MENTOR_APPROVED("mentorship_profile_approved_mentor.yml"),
  FEEDBACK_MENTOR("mentorship_programme_feedback_mentor.yml"),
  FEEDBACK_MENTEE("mentorship_programme_feedback_mentee.yml"),
  FINAL_UPDATES_MENTOR("final_updates_mentorship_cycle_mentor.yml"),
  CHECKIN_MENTEE("mentorship_checkin_announcement_mentee.yml"),
  MENTOR_AVAIL_ADHOC("confirm_month_availability_adhoc_mentor.yml"),
  NO_SLACK_MENTEE("not_on_slack_mentee_adhoc_application.yml"),
  PAIRING_TERMINATION("long_term_pairing_termination_mentor_mentee.yml"),
  NEG_FEEDBACK_MENTEE("negative_feedback_mentee_adhoc.yml"),
  MENTOR_DEACT_WARN("mentor_deactivation_warning.yml"),
  MENTEE_TERMIN_LONG("mentorship_termination_noshow_warning_mentee_long.yml"),
  ADHOC_SESSION_LINK("book_using_link_adhoc_session_with_mentor_mentee.yml"),
  ADHOC_SESSION_EMAIL("book_using_email_adhoc_session_with_mentor_mentee.yml"),
  MENTEE_LIST_LINK("potential_list_mentees_for_mentor_using_link.yml"),
  MENTEE_LIST_EMAIL("potential_list_mentees_for_mentor_using_email.yml"),
  MENTEE_FEEDBACK_ADHOC("reminder_adhoc_mentorship_feedback_mentee.yml"),
  RESET_PASSWORD("reset_password.yml");

  private final String templateFile;

  TemplateType(final String templateFile) {
    this.templateFile = templateFile;
  }
}
