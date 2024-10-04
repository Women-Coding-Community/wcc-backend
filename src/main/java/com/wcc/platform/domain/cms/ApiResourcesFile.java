package com.wcc.platform.domain.cms;

/** Create custom api configurations. */
public enum ApiResourcesFile {
  CODE_OF_CONDUCT("codeOfConductPage.json"),
  COLLABORATOR("collaboratorPage.json"),
  EVENTS("eventsPage.json"),
  FOOTER("footerPage.json"),
  LANDING_PAGE("landingPage.json"),
  MENTORSHIP("mentorshipPage.json"),
  TEAM("teamPage.json"),
  MENTORSHIP_FAQ("mentorshipFaq.json"),
  PROG_BOOK_CLUB("bookClubPage.json");

  private final String fileName;

  ApiResourcesFile(final String fileName) {
    this.fileName = fileName;
  }

  public String getFileName() {
    return fileName;
  }
}
