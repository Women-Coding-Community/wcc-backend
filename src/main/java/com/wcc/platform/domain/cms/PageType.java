package com.wcc.platform.domain.cms;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Define existent pages types. */
@Getter
@AllArgsConstructor
public enum PageType {
  CODE_OF_CONDUCT("codeOfConductPage.json", "page:CODE_OF_CONDUCT"),
  COLLABORATOR("collaboratorPage.json", "page:COLLABORATORS"),
  EVENTS("eventsPage.json", "page:EVENTS"),
  FOOTER("footerPage.json", "page:FOOTER"),
  LANDING_PAGE("landingPage.json", "page:LANDING_PAGE"),
  MENTORSHIP("mentorshipPage.json", "page:MENTORSHIP_OVERVIEW"),
  TEAM("teamPage.json", "page:TEAM"),
  PROG_BOOK_CLUB("bookClubPage.json", "UNDEFINED"),
  EVENT_FILTERS("eventsFiltersSection.json", "UNDEFINED"),
  ABOUT_US("aboutUsPage.json", "UNDEFINED");

  private final String fileName;
  private final String pageId;
}
