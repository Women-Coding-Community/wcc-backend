package com.wcc.platform.domain.template;

import lombok.Getter;

@Getter
public enum TemplateType {
    FEEDBACK_MENTOR_ADHOC("feedback_mentor_adhoc.yml"),
    FEEDBACK_MENTOR_LONG("feedback_mentor_long_term.yml"),
    NEW_CYCLE_MENTOR_LONG("joining_new_mentorship_cycle_mentor_long_term.yml"),
    WELCOME_MENTORSHIP_MENTEE_MENTOR_LONG("welcome_mentorship_mentee_mentor_long_term.yml"),
    KICK_OFF_MEETING_LONG("kick_off_meeting_mentor_mentee_long_term.yml"),
    MENTORSHIP_APPLICATION_RECEIVED_MENTEE_LONG(
        "long_term_mentorship_application_received_mentee.yml"),
    UNABLE_MATCH_MENTOR_LONG("mentee_unable_match_with_mentor_long.yml"),
    REMINDER_LONG_MENTORSHIP_MENTEE("reminder_long_term_mentorship_mentee.yml"),
    POTENTIAL_MENTEES_MENTOR_LONG("list_potential_mentees_mentor_long.yml"),
    FOLLOW_UP_POTENTIAL_MENTEES_MENTOR_LONG("list_potential_mentees_follow_up_mentor_long.yml"),
    NEW_MENTEES_ALERT_MENTOR_LONG("alert_new_mentees_applications_mentor_long.yml"),
    STUDY_GROUP_AVAILABILITY_MENTOR("confirm_availability_study_group_mentor.yml"),
    STUDY_GROUPS_MENTEE("mentor_led_study_groups_mentee.yml"),
    STUDY_GROUP_INTRODUCTION_MENTEE("study_group_introduction_email_mentee.yml"),
    NOT_ON_SLACK_MENTOR("not_on_slack_mentor_applicant.yml"),
    PROFILE_NOT_APPROVED_MENTOR("mentorship_profile_not_approved_mentor.yml"),
    PROFILE_APPROVED_MENTOR("mentorship_profile_approved_mentor.yml"),
    MENTORSHIP_FEEDBACK_MENTOR("mentorship_programme_feedback_mentor.yml"),
    MENTORSHIP_FEEDBACK_MENTEE("mentorship_programme_feedback_mentee.yml"),
    FINAL_UPDATES_MENTORSHIP_MENTOR("final_updates_mentorship_cycle_mentor.yml"),
    CHECKIN_ANNOUNCEMENT_MENTEE("mentorship_checkin_announcement_mentee.yml"),
    AVAILABILITY_MENTOR_ADHOC("confirm_month_availability_adhoc_mentor.yml"),
    NOT_ON_SLACK_MENTEE_ADHOC("not_on_slack_mentee_adhoc_application.yml"),
    PAIRING_TERMINATION_MENTOR_MENTEE("long_term_pairing_termination_mentor_mentee.yml"),
    NEGATIVE_FEEDBACK_MENTEE_ADHOC("negative_feedback_mentee_adhoc.yml"),
    MENTOR_DEACTIVATION_WARNING("mentor_deactivation_warning.yml"),
    MENTEE_TERMINATION_WARNING_LONG("mentorship_termination_noshow_warning_mentee_long.yml"),
    BOOK_ADHOC_SESSION_MENTEE_LINK("book_using_link_adhoc_session_with_mentor_mentee.yml"),
    BOOK_ADHOC_SESSION_MENTEE_EMAIL("book_using_email_adhoc_session_with_mentor_mentee.yml"),
    LIST_MENTEES_FOR_MENTOR_LINK("potential_list_mentees_for_mentor_using_link.yml"),
    LIST_MENTEES_FOR_MENTOR_EMAIL("potential_list_mentees_for_mentor_using_email.yml"),
    MENTORSHIP_FEEDBACK_MENTEE_ADHOC("reminder_adhoc_mentorship_feedback_mentee.yml");


    private final String templateFile;

    TemplateType(final String templateFile) {
        this.templateFile = templateFile;
    }
}
