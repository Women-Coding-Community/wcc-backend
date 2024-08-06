package com.wcc.platform.factories;

import com.wcc.platform.domain.cms.attributes.ProgramType;
import com.wcc.platform.domain.cms.attributes.SimpleLink;
import com.wcc.platform.domain.platform.Event;

/** Event test factories. */
public class SetupEventFactories {
  /**
   * Test factory for creating an event
   *
   * @return an object of {@link Event}
   */
  public static Event createEventTest() {
    return Event.builder()
        .title("Test event title for book club")
        .topics(ProgramType.BOOK_CLUB)
        .hostProfile(new SimpleLink("hostName", "http://host-profile-link"))
        .eventLink(new SimpleLink("Meetup", "http://meetup/link"))
        .startDate("THU, MAY 30, 2024, 8:00 PM CEST")
        .build();
  }
}
