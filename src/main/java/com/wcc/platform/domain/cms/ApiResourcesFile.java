package com.wcc.platform.domain.cms;

/** Create custom api configurations. */
public enum ApiResourcesFile {
  TEAM("teamPage.json"),
  LANDING_PAGE("landingPage.json"),
  MENTORSHIP("mentorshipPage.json"),
  FOOTER("footerPage.json"),
  COLLABORATOR("collaboratorPage.json"),
  CODE_OF_CONDUCT("codeOfConductPage.json"),
  EVENTS("eventsPage.json");

  private final String fileName;

  ApiResourcesFile(final String fileName) {
    this.fileName = fileName;
  }

  public String getFileName() {
    return fileName;
  }
}
