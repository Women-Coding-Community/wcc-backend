package com.wcc.platform.domain.platform.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** All available member types in the community. */
@Getter
@AllArgsConstructor
public enum MemberType {
  DIRECTOR(1),
  COLLABORATOR(2),
  EVANGELIST(3),
  LEADER(4),
  MENTEE(5),
  MENTOR(6),
  MEMBER(7),
  PARTNER(8),
  SPEAKER(9),
  VOLUNTEER(10);

  private final int typeId;

  /**
   * Retrieves the corresponding {@code MemberType} enum value based on a given type ID. If no match
   * is found, the default {@code MEMBER} type is returned.
   *
   * @param typeId the integer ID representing a specific {@code MemberType}
   * @return the {@code MemberType} that matches the given ID, or {@code MEMBER} if no match is
   *     found
   */
  public static MemberType fromId(final int typeId) {
    for (final MemberType type : values()) {
      if (type.getTypeId() == typeId) {
        return type;
      }
    }
    return MEMBER;
  }
}
