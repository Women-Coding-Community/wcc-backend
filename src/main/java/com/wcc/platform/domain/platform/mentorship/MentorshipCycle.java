package com.wcc.platform.domain.platform.mentorship;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

  public MentorshipCycle(final MentorshipType cycle) {
    this(cycle, null);
  }
}
