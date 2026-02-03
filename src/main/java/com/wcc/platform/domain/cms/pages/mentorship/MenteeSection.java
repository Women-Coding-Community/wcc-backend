package com.wcc.platform.domain.cms.pages.mentorship;

import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the Mentee Section of the Mentorship Page.
 *
 * @param idealMentee description of the ideal mentee for this mentor
 * @param additional additional information about mentorship
 * @param longTerm long-term mentorship commitment (null if not offering long-term)
 * @param adHoc list of monthly ad-hoc availability (empty if not offering ad-hoc)
 */
public record MenteeSection(
    @NotBlank String idealMentee,
    String additional,
    LongTermMentorship longTerm,
    List<MentorMonthAvailability> adHoc) {

  /**
   * Determines the mentorship types based on data presence.
   *
   * @return list of mentorship types this mentor offers
   */
  public List<MentorshipType> getMentorshipTypes() {
    final List<MentorshipType> types = new ArrayList<>();
    if (longTerm != null) {
      types.add(MentorshipType.LONG_TERM);
    }
    if (adHoc != null && !adHoc.isEmpty()) {
      types.add(MentorshipType.AD_HOC);
    }
    return types;
  }

  /**
   * Converts the current MenteeSection instance into a new MenteeSection DTO. The DTO excludes
   * ad-hoc availability details for public display.
   *
   * @return a new MenteeSection instance with the same idealMentee, additional, and longTerm
   *     fields, but with an empty adHoc list.
   */
  public MenteeSection toDto() {
    return new MenteeSection(idealMentee, additional, longTerm, List.of());
  }
}
