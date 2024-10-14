package com.wcc.platform.domain.cms;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Define existent pages types. */
@Getter
@AllArgsConstructor
public enum PageType {
  CODE_OF_CONDUCT("codeOfConductPage.json"),
  COLLABORATOR("collaboratorPage.json"),
  EVENTS("eventsPage.json"),
  FOOTER("footerPage.json"),
  LANDING_PAGE("landingPage.json"),
  MENTORSHIP("mentorshipPage.json"),
  TEAM("teamPage.json"),
  PROG_BOOK_CLUB("bookClubPage.json");

  private final String fileName;
}
