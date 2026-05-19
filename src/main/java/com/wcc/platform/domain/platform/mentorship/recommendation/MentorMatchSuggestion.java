package com.wcc.platform.domain.platform.mentorship.recommendation;

import com.wcc.platform.domain.platform.mentorship.Mentor;
import java.util.List;

/**
 * DTO for a mentor and their suggested mentee matches with scores.
 *
 * @param mentor the mentor object
 * @param mentees list of suggested mentee matches with scores
 */
public record MentorMatchSuggestion(Mentor mentor, List<MenteeMatchSuggestion> mentees) {}
