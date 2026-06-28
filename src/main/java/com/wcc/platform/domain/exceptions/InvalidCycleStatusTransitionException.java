package com.wcc.platform.domain.exceptions;

import com.wcc.platform.domain.platform.mentorship.CycleStatus;

/** Exception thrown when an invalid mentorship cycle status transition is attempted. */
public class InvalidCycleStatusTransitionException extends RuntimeException {

  /**
   * Constructor with current and requested status.
   *
   * @param current the current cycle status
   * @param requested the requested target status
   */
  public InvalidCycleStatusTransitionException(
      final CycleStatus current, final CycleStatus requested) {
    super(
        "Invalid status transition from '%s' to '%s'".formatted(current.name(), requested.name()));
  }
}
