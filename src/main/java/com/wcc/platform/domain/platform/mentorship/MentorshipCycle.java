package com.wcc.platform.domain.platform.mentorship;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wcc.platform.domain.cms.pages.mentorship.MentorsPage;
import java.time.Month;

/**
 * Represents a mentorship cycle with a specific type and associated month.
 *
 * <p>This class encapsulates the type of mentorship cycle and the corresponding month to provide an
 * immutable representation of a mentorship cycle's duration and categorization.
 *
 * @param cycle The type of mentorship cycle, which could be either AD_HOC or LONG_TERM.
 * @param month The month associated with this mentorship cycle.
 */
public record MentorshipCycle(MentorshipType cycle, @JsonIgnore Month month) {

  public MentorshipCycle() {
    this(null, null);
  }

  public MentorshipCycle(final MentorshipType cycle) {
    this(cycle, null);
  }

  /**
   * Converts the MentorshipCycle to an OpenCycle if the cycle is either LONG_TERM or AD_HOC.
   *
   * @return An instance of MentorsPage.OpenCycle if the cycle is LONG_TERM or AD_HOC; otherwise,
   *     returns null.
   */
  public MentorsPage.OpenCycle toOpenCycle() {
    if (cycle == null) {
      return new MentorsPage.OpenCycle(null, false);
    }

    return new MentorsPage.OpenCycle(cycle(), true);
  }
}
