package com.wcc.platform.domain.platform.member;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProfileStatus {
  ACTIVE(1),
  DISABLED(2),
  BANNED(3);

  private final int status_id;

  public static ProfileStatus fromId(int id) {
    return Arrays.stream(values())
        .filter(status -> status.status_id == id)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unknown ProfileStatus id: " + id));
  }
}
