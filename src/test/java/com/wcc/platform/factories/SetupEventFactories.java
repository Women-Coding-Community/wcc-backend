package com.wcc.platform.factories;

import static com.wcc.platform.factories.SetupFactories.createContactTest;
import static com.wcc.platform.factories.SetupFactories.createImageTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wcc.platform.domain.cms.attributes.HeroSection;
import com.wcc.platform.domain.cms.attributes.LabelLink;
import com.wcc.platform.domain.cms.pages.EventsPage;
import com.wcc.platform.domain.platform.Event;
import com.wcc.platform.domain.platform.EventSection;
import com.wcc.platform.domain.platform.ProgramType;
import com.wcc.platform.utils.FileUtil;
import java.util.Collections;
import java.util.List;

/** Event test factories. */
public class SetupEventFactories {

  /** Event page test. */
  public static EventsPage createEventTest(final String fileName) {
    try {
      final String content = FileUtil.readFileAsString(fileName);
      return SetupFactories.OBJECT_MAPPER.readValue(content, EventsPage.class);
    } catch (JsonProcessingException e) {
      return createEventPageTest(Collections.singletonList(createEventTest(ProgramType.BOOK_CLUB)));
    }
  }

  /** Event page test. */
  public static Event createEventTest() {
    return createEventTest(ProgramType.BOOK_CLUB);
  }

  /** Event page test. */
  public static Event createEventTest(final ProgramType programType) {
    return Event.builder()
        .title("Test event title for " + programType)
        .topics(programType)
        .hostProfile(new LabelLink(null, "hostName", "http://host-profile-link"))
        .title("title")
        .description("description")
        .speakerProfile(new LabelLink(null, "speaker", "http://speaker-profile-link"))
        .eventLink(new LabelLink(null, "Meetup", "http://meetup/link"))
        .startDate("THU, MAY 30, 2024, 8:00 PM CEST")
        .build();
  }

  /** Event page test. */
  public static EventsPage createEventPageTest(final List<Event> events) {
    var hero = new HeroSection("title", "event description", createImageTest());
    return new EventsPage(events, hero, createContactTest());
  }

  /**
   * Create an EventSection object with test data.
   *
   * @return EventSection object
   */
  public static EventSection createEventSectionTest() {
    return EventSection.builder()
        .title("Upcoming Events")
        .link(new LabelLink(null, "view events", "/events"))
        .events(Collections.singletonList(createEventTest()))
        .build();
  }

  /**
   * Create an EventSection object with test data.
   *
   * @return EventSection object
   */
  public static EventSection createEventSectionTest(final ProgramType programType) {
    return EventSection.builder()
        .title("Upcoming Events")
        .link(new LabelLink(null, "view events", "/events"))
        .events(Collections.singletonList(createEventTest(programType)))
        .build();
  }
}
