package com.wcc.platform.domain.platform.mentorship;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.Year;
import java.util.List;

/**
 * Represents the registration process for a mentee within the mentorship program. This record
 * encapsulates the mentee's details, the type of mentorship they are registering for, and a list of
 * mentors assigned to them.
 *
 * <p>Components: - mentee: An instance of the {@link Mentee} class, representing the mentee's
 * profile and associated details. - mentorshipType: The type of mentorship the mentee is
 * registering for, represented by {@link MentorshipType}. Determines whether the mentorship is
 * short-term (AD_HOC) or long-term (LONG_TERM). - mentorIds: A list of unique IDs representing the
 * mentors assigned to the mentee.
 *
 * <p>Validation Constraints: - The mentee field must not be null. - The mentorshipType field must
 * not be null. - The mentorIds list must not be empty.
 */
public record MenteeRegistration(
    @NotNull Mentee mentee,
    @NotNull MentorshipType mentorshipType,
    @NotNull Year cycleYear,
    @Max(5) @Min(1) List<Long> mentorIds) {

  public List<MenteeApplication> toApplications(MentorshipCycleEntity cycle) {
    return mentorIds.stream()
        .map(
            mentorId ->
                MenteeApplication.builder()
                    .menteeId(mentee.getId())
                    .mentorId(mentorId)
                    .cycleId(cycle.getCycleId())
                    .build())
        .toList();
  }
}
