package com.wcc.platform.domain.platform.mentorship;

/**
 * Overall mentor availability for the mentorship cycle.
 *
 * @param mentorshipType Can be AD-HOC or long-term
 * @param available can be true or false depending on the mentorship cycle
 */
public record MentorAvailability(MentorshipType mentorshipType, boolean available) {}
