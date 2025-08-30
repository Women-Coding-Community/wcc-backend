package com.wcc.platform.factories;

import static com.wcc.platform.factories.SetupFactories.createCommonSectionTest;
import static com.wcc.platform.factories.SetupFactories.createContactTest;
import static com.wcc.platform.factories.SetupFactories.createHeroSectionTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wcc.platform.domain.cms.PageType;
import com.wcc.platform.domain.cms.attributes.LabelLink;
import com.wcc.platform.domain.cms.pages.PageData;
import com.wcc.platform.domain.cms.pages.PageMetadata;
import com.wcc.platform.domain.cms.pages.Pagination;
import com.wcc.platform.domain.cms.pages.events.EventsPage;
import com.wcc.platform.domain.platform.Event;
import com.wcc.platform.domain.platform.EventSection;
import com.wcc.platform.domain.platform.type.ProgramType;
import com.wcc.platform.utils.FileUtil;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

/** Event test factories. */
public class SetupEventFactories {

  public static final int DEFAULT_CURRENT_PAGE = 1;
  public static final int DEFAULT_PAGE_SIZE = 10;

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
        .startDate(ZonedDateTime.of(2024, 5, 30, 20, 0, 0, 0, ZoneId.of("Europe/Paris")))
        .build();
  }

  /** Event page test. */
  public static EventsPage createEventPageTest(final List<Event> items) {
    var metadata =
        new PageMetadata(createPaginationTest(items, DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE));
    var data = new PageData<>(items);
    return new EventsPage(
        PageType.EVENTS.getId(),
        metadata,
        createHeroSectionTest(),
        createCommonSectionTest(),
        createContactTest(),
        data);
  }

  /**
   * Create an EventSection object with test data.
   *
   * @return EventSection object
   */
  public static EventSection createEventSection() {
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
  public static EventSection createEventSection(final ProgramType programType) {
    return EventSection.builder()
        .title("Upcoming Events")
        .link(new LabelLink(null, "view events", "/events"))
        .events(Collections.singletonList(createEventTest(programType)))
        .build();
  }

  /**
   * Create pagination metadata for the events page.
   *
   * @return pagination metadata
   */
  public static Pagination createPaginationTest(
      final List<Event> items, final int currentPage, final int pageSize) {
    int totalItems = items.size();
    int totalPages = (int) Math.ceil((double) totalItems / pageSize);
    return new Pagination(totalItems, totalPages, currentPage, pageSize);
  }
}
