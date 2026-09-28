package com.wcc.platform.domain.platform.mentorship;

import com.wcc.platform.domain.platform.member.ProfileStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Year;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

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
    @Schema(
            type = "integer",
            format = "int32",
            example = "2026",
            description = "The year of the mentorship cycle")
        Year cycleYear,
    @Size(min = 1, max = 5) List<MenteeApplicationDto> applications) {

  /**
   * Converts a list of {@code MenteeApplicationDto} to a list of {@code MenteeApplication}.
   *
   * <p>For AD_HOC cycles, applications are set to {@code MENTOR_REVIEWING} only when the mentee
   * profile is already {@code ACTIVE}. A {@code PENDING} profile means the mentee has not yet been
   * reviewed by an admin, so applications stay in {@code PENDING} until the profile is activated.
   *
   * @param cycle the current mentorship cycle
   * @param menteeId the mentee's ID
   * @param profileStatus the persisted profile status of the mentee
   * @return list of applications with the correct initial status
   */
  public List<MenteeApplication> toApplications(
      final MentorshipCycleEntity cycle, final Long menteeId, final ProfileStatus profileStatus) {
    return applications.stream()
        .map(
            application -> {
              final var builder = MenteeApplication.builder();

              if (StringUtils.isNotBlank(application.applicationMessage())) {
                builder.applicationMessage(application.applicationMessage());
              }

              final var initialStatus =
                  cycle.getMentorshipType() == MentorshipType.AD_HOC
                          && profileStatus == ProfileStatus.ACTIVE
                      ? ApplicationStatus.MENTOR_REVIEWING
                      : ApplicationStatus.PENDING;

              return builder
                  .menteeId(menteeId)
                  .mentorId(application.mentorId())
                  .whyMentor(application.whyMentor())
                  .priorityOrder(application.priorityOrder())
                  .status(initialStatus)
                  .cycleId(cycle.getCycleId())
                  .build();
            })
        .toList();
  }

  public MenteeRegistration withApplications(final List<MenteeApplicationDto> applications) {
    return new MenteeRegistration(mentee, mentorshipType, cycleYear, applications);
  }
}
