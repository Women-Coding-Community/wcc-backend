package com.wcc.platform.domain.platform;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** Supported network types. */
@Getter
@AllArgsConstructor
public enum SocialNetworkType {
  DEFAULT_LINK(1),
  EMAIL(2),
  FACEBOOK(3),
  GITHUB(4),
  INSTAGRAM(5),
  LINKEDIN(6),
  MEETUP(7),
  MEDIUM(8),
  SLACK(9),
  UNKNOWN(10),
  WEBSITE(11),
  YOUTUBE(12);

  private final int typeId;

  /** Get SocialNetworkType from its typeId. */
  public static SocialNetworkType fromId(final int typeId) {
    return Arrays.stream(values())
        .filter(type -> type.typeId == typeId)
        .findFirst()
        .orElse(UNKNOWN);
  }

  /** Always serialize to lowercase JSON. */
  @JsonValue
  public String toJson() {
    return name().toLowerCase();
  }
}
