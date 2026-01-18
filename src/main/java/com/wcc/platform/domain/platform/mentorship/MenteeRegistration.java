package com.wcc.platform.domain.platform.mentorship;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @Size(min = 1, max = 5) List<MenteeApplicationDto> applications) {

  public List<MenteeApplication> toApplications(MentorshipCycleEntity cycle, Long menteeId) {
    return applications.stream()
        .map(
            application ->
                MenteeApplication.builder()
                    .menteeId(menteeId)
                    .mentorId(application.mentorId())
                    .priorityOrder(application.priorityOrder())
                    .status(ApplicationStatus.PENDING)
                    .cycleId(cycle.getCycleId())
                    .build())
        .toList();
  }

  public MenteeRegistration withApplications(List<MenteeApplicationDto> applications) {
    return new MenteeRegistration(mentee, mentorshipType, cycleYear, applications);
  }
}
