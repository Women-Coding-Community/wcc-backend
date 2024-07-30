package com.wcc.platform.domain.cms;

/** Create custom api configurations. */
public enum ApiResourcesFile {
  CODE_OF_CONDUCT("codeOfConductPage.json"),
  COLLABORATOR("collaboratorPage.json"),
  FOOTER("footerPage.json"),
  MENTORSHIP("mentorshipPage.json"),

  PROG_BOOK_CLUB("bookClubPage.json"),
  TEAM("teamPage.json");

  private final String fileName;

  ApiResourcesFile(final String fileName) {
    this.fileName = fileName;
  }

  public String getFileName() {
    return fileName;
  }
}
