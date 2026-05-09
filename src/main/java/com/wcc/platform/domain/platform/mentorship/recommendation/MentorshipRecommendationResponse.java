package com.wcc.platform.domain.platform.mentorship.recommendation;

import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import java.util.List;

/**
 * DTO for recommended matches response.
 *
 * @param matchedMentors list of mentors with their suggested mentee matches
 * @param notMatchedMentors list of mentors who have no suggested matches
 * @param notMatchedMentees list of mentees who are not suggested for any mentor
 */
public record MentorshipRecommendationResponse(
    List<MentorMatchSuggestion> matchedMentors,
    List<Mentor> notMatchedMentors,
    List<Mentee> notMatchedMentees) {}
