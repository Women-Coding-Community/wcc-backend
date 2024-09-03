package com.wcc.platform.repository.file.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Create custom configuration for file repository. */
@Getter
@AllArgsConstructor
public enum RepositoryConfigFile {
  MEMBERS_FILE("repository/members.json");

  private final String fileName;
}
