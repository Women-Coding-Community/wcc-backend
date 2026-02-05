package com.wcc.platform.domain.cms.pages.mentorship;

import jakarta.validation.constraints.Min;

/**
 * Represents the long-term mentorship commitment.
 *
 * @param numMentee number of mentees the mentor can support (minimum 1)
 * @param hours total hours committed for long-term mentorship (minimum 2 hours per mentee)
 */
public record LongTermMentorship(@Min(1) Integer numMentee, @Min(2) Integer hours) {}
