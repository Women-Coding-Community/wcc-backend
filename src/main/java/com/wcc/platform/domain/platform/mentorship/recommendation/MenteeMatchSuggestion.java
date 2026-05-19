package com.wcc.platform.domain.platform.mentorship.recommendation;

import com.wcc.platform.domain.platform.mentorship.ApplicationStatus;
import com.wcc.platform.domain.platform.mentorship.Mentee;

/**
 * DTO for a suggested mentee and their match score.
 *
 * @param mentee the mentee object
 * @param score the match score
 * @param applicationStatus the application status
 */
public record MenteeMatchSuggestion(Mentee mentee, int score, ApplicationStatus applicationStatus) {
  public MenteeMatchSuggestion(final Mentee mentee, final int score) {
    this(mentee, score, null);
  }
}
