package com.wcc.platform.domain.platform.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** List of available servers. */
@Getter
@AllArgsConstructor
public enum PlatformServers {
  LOCAL("http://localhost:8080"),
  DEV("https://wcc-backend.fly.dev");

  private final String uri;
}
