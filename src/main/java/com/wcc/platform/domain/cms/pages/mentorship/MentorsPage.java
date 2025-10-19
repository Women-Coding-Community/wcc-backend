package com.wcc.platform.domain.cms.pages.mentorship;

import com.wcc.platform.domain.cms.attributes.HeroSection;
import com.wcc.platform.domain.platform.mentorship.MentorDto;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * CMS Mentors Page details.
 *
 * @param heroSection hero section to be shown on the mentors page
 * @param mentors section to highlight why you apply to become a mentor
 */
public record MentorsPage(
    @NotNull String id,
    @NotNull HeroSection heroSection,
    OpenCycle openCycle,
    MentorFilterSection filterSection,
    @NotNull List<MentorDto> mentors) {

  public MentorsPage updateUpdate(
      final OpenCycle openCycle,
      final MentorFilterSection filterSection,
      final List<MentorDto> mentors) {
    return new MentorsPage(id, heroSection, openCycle, filterSection, mentors);
  }

  /**
   * Represents an open cycle for mentorship activities.
   *
   * <p>This record is used to signify whether a cycle is currently active and the type of
   * mentorship it pertains to, such as short-term (ad-hoc) or long-term arrangements.
   *
   * @param mentorshipType the type of mentorship available in the cycle
   * @param active indicates if the cycle is open or closed
   */
  public record OpenCycle(MentorshipType mentorshipType, boolean active) {
    public OpenCycle(final MentorshipType mentorshipType) {
      this(mentorshipType, true);
    }

    public OpenCycle(final Boolean active) {
      this(null, active);
    }
  }
}
