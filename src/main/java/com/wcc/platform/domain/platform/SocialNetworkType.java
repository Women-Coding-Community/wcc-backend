package com.wcc.platform.domain.platform;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** Supported network types. */
@Getter
@AllArgsConstructor
public enum SocialNetworkType {
  default_link(1),
  email(2),
  facebook(3),
  github(4),
  instagram(5),
  linkedin(6),
  meetup(7),
  medium(8),
  slack(9),
  unknown(10),
  website(11),
  youtube(12);

  private final int typeId;

  /** Get SocialNetworkType from its typeId. */
  public static SocialNetworkType fromId(final int typeId) {
    return Arrays.stream(values())
        .filter(type -> type.typeId == typeId)
        .findFirst()
        .orElse(unknown);
  }
}
