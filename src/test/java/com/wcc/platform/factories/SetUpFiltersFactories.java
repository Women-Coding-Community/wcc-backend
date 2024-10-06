package com.wcc.platform.factories;

import com.wcc.platform.domain.cms.attributes.EventDays;
import com.wcc.platform.domain.cms.attributes.EventType;
import com.wcc.platform.domain.cms.pages.FiltersSection;
import com.wcc.platform.domain.platform.Filters;
import com.wcc.platform.domain.platform.ProgramType;
import java.util.List;

/** Filters set-up factories. */
public class SetUpFiltersFactories {

  /**
   * Create the filters object.
   *
   * @return {@link Filters}
   */
  public static Filters createFilterTest() {
    return Filters.builder()
        .type(List.of(EventType.IN_PERSON))
        .topics(List.of(ProgramType.BOOK_CLUB))
        .date(List.of(EventDays.IN_30_DAYS))
        .location(List.of("London", "Spain"))
        .build();
  }

  /**
   * Create filters by eventType - eg : IN_PERSON, ONLINE
   *
   * @param eventType - {@link EventType} IN_PERSON, ONLINE
   * @return - {@link Filters}
   */
  public static Filters createFilterTest(final List<EventType> eventType) {
    return Filters.builder()
        .type(eventType)
        .topics(List.of(ProgramType.BOOK_CLUB))
        .date(List.of(EventDays.IN_30_DAYS))
        .location(List.of("London", "Spain"))
        .build();
  }

  /**
   * Create the filters section object
   *
   * @return {@link FiltersSection}
   */
  public static FiltersSection createFilterSectionTest() {
    return new FiltersSection("default_title", createFilterTest());
  }
}
