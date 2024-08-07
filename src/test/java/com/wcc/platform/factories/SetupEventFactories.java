package com.wcc.platform.factories;

import static com.wcc.platform.factories.SetupFactories.createContactTest;
import static com.wcc.platform.factories.SetupFactories.createImageTest;

import com.wcc.platform.domain.cms.attributes.HeroSection;
import com.wcc.platform.domain.cms.attributes.ProgramType;
import com.wcc.platform.domain.cms.attributes.SimpleLink;
import com.wcc.platform.domain.cms.pages.EventsPage;
import com.wcc.platform.domain.platform.Event;
import java.util.List;

public class SetupEventFactories {

  /** Event page test. */
  public static Event createEventTest() {
    return createEventTest(ProgramType.BOOK_CLUB);
  }

  /** Event page test. */
  public static Event createEventTest(ProgramType programType) {
    return Event.builder()
        .title("Test event title for " + programType)
        .topics(programType)
        .hostProfile(new SimpleLink("hostName", "http://host-profile-link"))
        .host("host")
        .speaker("speaker")
        .title("title")
        .description("description")
        .speakerProfile(new SimpleLink("speaker", "http://speaker-profile-link"))
        .eventLink(new SimpleLink("Meetup", "http://meetup/link"))
        .startDate("THU, MAY 30, 2024, 8:00 PM CEST")
        .build();
  }

  /** Event page test. */
  public static EventsPage createEventPageTest(List<Event> events) {
    var hero = new HeroSection("title", "event description", createImageTest());
    return new EventsPage(events, hero, createContactTest());
  }
}
