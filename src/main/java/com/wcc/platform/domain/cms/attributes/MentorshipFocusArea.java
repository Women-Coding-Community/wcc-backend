package com.wcc.platform.domain.cms.attributes;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum representing various mentee focus areas.
 *
 * <p>Each enum instance represents a specific career focus or growth goal for a mentee, with a
 * descriptive name and a unique identifier.
 */
@Getter
@AllArgsConstructor
public enum MentorshipFocusArea {
  SWITCH_CAREER_TO_IT("Switch career to IT", 1),
  GROW_BEGINNER_TO_MID("Grow from beginner to mid-level", 2),
  GROW_MID_TO_SENIOR("Grow from mid-level to senior-level", 3),
  GROW_BEYOND_SENIOR("Grow beyond senior level", 4),
  SWITCH_TO_MANAGEMENT("Switch from IC to management position", 5),
  CHANGE_SPECIALISATION("Change specialisation within IT", 6);

  private final String description;
  private final int focusId;

  /** Find focus by id. */
  public static MentorshipFocusArea fromId(final Integer id) {
    for (final MentorshipFocusArea focus : values()) {
      if (focus.focusId == id) {
        return focus;
      }
    }
    throw new IllegalArgumentException("Unknown Mentorship Focus ID: " + id);
  }

  public static List<MentorshipFocusArea> getAll() {
    return List.of(values());
  }

  @Override
  public String toString() {
    return description;
  }
}
