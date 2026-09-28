package com.wcc.platform.domain.cms.pages.mentorship;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycle;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
   * Determines the mentorship types based on data presence. This is for internal use only and is
   * not serialized to JSON.
   *
   * @return list of mentorship types this mentor offers
   */
  @JsonIgnore
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
   * Converts the current MenteeSection instance into a new MenteeSection DTO.
   *
   * @return a new MenteeSection instance with the same idealMentee, additional, longTerm and ad-hoc
   *     availabilities.
   */
  public MenteeSection toDto() {
    return new MenteeSection(idealMentee, additional, longTerm, adHoc);
  }

  /**
   * Converts to a DTO scoped to the given mentorship cycle. For AD_HOC cycles the adHoc
   * availability list is filtered to only the entries whose month matches the cycle month and that
   * declare more than zero hours, so that mentors unavailable in the current cycle month (or with no
   * real availability, i.e. 0 hours) are not surfaced by the AD_HOC type filter.
   *
   * @param cycle the active mentorship cycle; when null or LONG_TERM the full adHoc list is kept
   * @return a MenteeSection DTO with adHoc availability filtered to the current cycle month and to
   *     entries with positive hours
   */
  public MenteeSection toDtoForCycle(final MentorshipCycle cycle) {
    if (cycle == null
        || cycle.cycle() != MentorshipType.AD_HOC
        || cycle.month() == null
        || adHoc == null) {
      return toDto();
    }
    final List<MentorMonthAvailability> filteredAdHoc =
        adHoc.stream()
            .filter(a -> Objects.equals(a.month(), cycle.month()))
            .filter(a -> a.hours() != null && a.hours() > 0)
            .toList();
    return new MenteeSection(idealMentee, additional, longTerm, filteredAdHoc);
  }
}
