package com.wcc.platform.domain.platform;

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
    return this.name().toLowerCase();
  }
}
