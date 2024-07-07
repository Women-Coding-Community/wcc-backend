package com.wcc.platform.domain.platform;

import java.util.Locale;

/** Community available network types. */
public enum SocialNetworkType {
  YOUTUBE,
  GITHUB,
  LINKEDIN,
  INSTAGRAM,
  FACEBOOK,
  X,
  MEDIUM,
  SLACK,
  MEETUP,
  EMAIL,
  UNKNOWN;

  @Override
  public String toString() {
    return this.name().toLowerCase(Locale.getDefault());
  }
}
