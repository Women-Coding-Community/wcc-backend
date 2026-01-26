package com.wcc.platform.domain.platform.mentorship;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Year;
import java.util.List;

/**
 * Represents a mentee registration with mentorship preferences and mentor applications.
 *
 * @param mentee The mentee profile
 * @param mentorshipType The type of mentorship (AD_HOC or LONG_TERM)
 * @param cycleYear The year of the mentorship cycle
 * @param applications List of mentor applications with priority order (1-5)
 */
public record MenteeRegistration(
    @NotNull Mentee mentee,
    @NotNull MentorshipType mentorshipType,
    @NotNull Year cycleYear,
    @Size(min = 1, max = 5) List<MenteeApplicationDto> applications) {

  public List<MenteeApplication> toApplications(
      final MentorshipCycleEntity cycle, final Long menteeId) {
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

  public MenteeRegistration withApplications(final List<MenteeApplicationDto> applications) {
    return new MenteeRegistration(mentee, mentorshipType, cycleYear, applications);
  }

  public MenteeRegistration withMentee(final Mentee mentee) {
    return new MenteeRegistration(mentee, mentorshipType, cycleYear, applications);
  }
}
