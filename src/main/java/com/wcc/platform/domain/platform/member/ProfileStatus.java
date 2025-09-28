package com.wcc.platform.domain.platform.member;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** Member & Mentor profile status. */
@Getter
@AllArgsConstructor
public enum ProfileStatus {
  ACTIVE(1),
  DISABLED(2),
  BANNED(3),
  PENDING(4);

  private final int statusId;

  /** Get ProfileStatus from its status ID. */
  public static ProfileStatus fromId(int statusId) {
    return Arrays.stream(values())
        .filter(status -> status.statusId == statusId)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unknown ProfileStatus id: " + statusId));
  }
}
