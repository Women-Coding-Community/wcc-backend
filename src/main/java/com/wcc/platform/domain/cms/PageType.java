package com.wcc.platform.domain.cms;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Define existent pages types. */
@Getter
@AllArgsConstructor
@SuppressWarnings("PMD.LongVariable")
public enum PageType {
  EVENTS("eventsPage.json", "page:EVENTS"),
  FOOTER("footerPage.json", "page:FOOTER"),
  LANDING_PAGE("landingPage.json", "page:LANDING_PAGE"),
  MENTORSHIP("mentorshipPage.json", "page:MENTORSHIP_OVERVIEW"),
  MENTORSHIP_FAQ("mentorshipPage.json", "page:MENTORSHIP_FAQ"),
  MENTORSHIP_CONDUCT("mentorshipConductPage.json", "page:MENTORSHIP_CODE_OF_CONDUCT"),
  PROG_BOOK_CLUB("bookClubPage.json", "page:BOOK_CLUB"),
  EVENT_FILTERS("eventsFiltersSection.json", "UNDEFINED"),
  ABOUT_US("aboutUsPage.json", "page:ABOUT_US"),
  TEAM("teamPage.json", "page:TEAM"),
  COLLABORATOR("collaboratorPage.json", "page:COLLABORATORS"),
  CODE_OF_CONDUCT("codeOfConductPage.json", "page:CODE_OF_CONDUCT");

  public static final String ID_PREFIX = "page:";

  private final String fileName;
  private final String id;
}
