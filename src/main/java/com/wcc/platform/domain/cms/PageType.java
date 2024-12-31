package com.wcc.platform.domain.cms;

import static com.wcc.platform.repository.PageRepository.ID_PREFIX;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Define existent pages types. */
@Getter
@AllArgsConstructor
public enum PageType {
  EVENTS("eventsPage.json", ID_PREFIX + "EVENTS"),
  FOOTER("footerPage.json", ID_PREFIX + "FOOTER"),
  LANDING_PAGE("landingPage.json", ID_PREFIX + "LANDING_PAGE"),
  MENTORSHIP("mentorshipPage.json", ID_PREFIX + "MENTORSHIP_OVERVIEW"),
  PROG_BOOK_CLUB("bookClubPage.json", ID_PREFIX + "BOOK_CLUB"),
  EVENT_FILTERS("eventsFiltersSection.json", "UNDEFINED"),
  ABOUT_US("aboutUsPage.json", ID_PREFIX + "ABOUT_US"),
  TEAM("teamPage.json", ID_PREFIX + "TEAM"),
  COLLABORATOR("collaboratorPage.json", ID_PREFIX + "COLLABORATORS"),
  CODE_OF_CONDUCT("codeOfConductPage.json", ID_PREFIX + "CODE_OF_CONDUCT");

  private final String fileName;
  private final String pageId;
}
