package com.wcc.platform.domain.cms.pages.mentorship;

import java.time.Year;

/**
 * Mentorship individual Feedbacks.
 *
 * @param name Mentee/Mentor Name
 * @param feedback description of the feedback
 * @param mentee flag to identify if it was a mentee or mentor feedback.
 * @param year which year the feedback was given.
 */
public record FeedbackItem(String name, String feedback, boolean mentee, Year year) {}
