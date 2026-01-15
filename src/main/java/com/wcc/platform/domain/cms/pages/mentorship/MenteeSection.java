package com.wcc.platform.domain.cms.pages.mentorship;

import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/** Represents the Mentee Section of the Mentorship Page. */
public record MenteeSection(
    @NotEmpty List<MentorshipType> mentorshipType,
    List<MentorMonthAvailability> availability,
    @NotBlank String idealMentee,
    String additional) {

  /**
   * Converts the current MenteeSection instance into a new MenteeSection DTO.
   *
   * @return a new MenteeSection instance containing the same mentorshipType, idealMentee, focus,
   *     and additional fields, but with an empty availability list.
   */
  public MenteeSection toDto() {
    return new MenteeSection(mentorshipType, List.of(), idealMentee, additional);
  }
}
