package com.wcc.platform.domain.cms;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Define existent pages types. */
@Getter
@AllArgsConstructor
public enum PageType {
  EVENTS("eventsPage.json", "page:EVENTS"),
  FOOTER("footerPage.json", "page:FOOTER"),
  LANDING_PAGE("landingPage.json", "page:LANDING_PAGE"),
  MENTORSHIP("mentorshipPage.json", "page:MENTORSHIP_OVERVIEW"),
  PROG_BOOK_CLUB("bookClubPage.json", "page:BOOK_CLUB"),
  EVENT_FILTERS("eventsFiltersSection.json", "UNDEFINED"),
  ABOUT_US("aboutUsPage.json", "page:ABOUT_US"),
  CELEBRATE_HER("celebrateHerPage.json", "page:CELEBRATE_HER_PAGE"),
  TEAM("teamPage.json", "page:TEAM"),
  COLLABORATOR("collaboratorPage.json", "page:COLLABORATORS"),
  CODE_OF_CONDUCT("codeOfConductPage.json", "page:CODE_OF_CONDUCT");

  public static final String ID_PREFIX = "page:";

  private final String fileName;
  private final String id;
}
