package com.wcc.platform.domain.platform.mentorship.recommendation;

import com.wcc.platform.domain.platform.mentorship.Mentee;

/**
 * DTO for a suggested mentee and their match score.
 *
 * @param mentee the mentee object
 * @param score the match score
 */
public record MenteeMatchSuggestion(Mentee mentee, int score) {}
