package com.wcc.platform.domain.platform;

import static com.wcc.platform.factories.SetUpFiltersFactories.createFilterTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wcc.platform.domain.cms.attributes.EventType;
import com.wcc.platform.domain.cms.pages.events.Filters;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FiltersTest {

  private Filters filters;

  @BeforeEach
  void setup() {
    filters = createFilterTest();
  }

  @Test
  void checkEquals() {
    assertEquals(filters, createFilterTest());
  }

  @Test
  void checkNotEquals() {
    assertNotEquals(
        filters, createFilterTest(List.of(EventType.IN_PERSON, EventType.ONLINE_MEETUP)));
  }

  @Test
  void checkHashCode() {
    assertEquals(filters.hashCode(), createFilterTest(List.of(EventType.IN_PERSON)).hashCode());
  }

  @Test
  void checkHashCodeDoNotMatch() {
    assertNotEquals(filters.hashCode(), createFilterTest(List.of(EventType.HYBRID)).hashCode());
  }

  @Test
  void checkToString() {
    assertTrue(filters.toString().contains(EventType.IN_PERSON.toString()));
  }
}
