package com.wcc.platform.repository.file.config;

/** Create custom configuration for file repository. */
public enum RepositoryConfigFile {
  MEMBERS_FILE("repository/members.json");

  private final String fileName;

  RepositoryConfigFile(String fileName) {
    this.fileName = fileName;
  }

  public String getFileName() {
    return fileName;
  }
}
