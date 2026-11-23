package com.wcc.platform.service;

import static com.wcc.platform.factories.SetupEventFactories.DEFAULT_CURRENT_PAGE;
import static com.wcc.platform.factories.SetupEventFactories.DEFAULT_PAGE_SIZE;
import static com.wcc.platform.factories.SetupEventFactories.createEventPageTest;
import static com.wcc.platform.factories.SetupEventFactories.createEventTest;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wcc.platform.domain.cms.PageType;
import com.wcc.platform.domain.cms.pages.events.EventsPage;
import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.repository.PageRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class EventServiceTest {

  private ObjectMapper objectMapper;
  private EventService service;
  private PageRepository pageRepository;

  @BeforeEach
  void setUp() {
    objectMapper = Mockito.mock(ObjectMapper.class);
    pageRepository = Mockito.mock(PageRepository.class);

    service = new EventService(objectMapper, pageRepository);
  }

  @Test
  void whenGetEventsGivenRecordExistingInDatabaseThenReturnValidResponse() {
    var eventsPage = createEventPageTest(List.of(createEventTest()));
    var mapPage =
        new ObjectMapper().registerModule(new JavaTimeModule()).convertValue(eventsPage, Map.class);

    when(pageRepository.findById(PageType.EVENTS.getId())).thenReturn(Optional.of(mapPage));
    when(objectMapper.convertValue(anyMap(), eq(EventsPage.class))).thenReturn(eventsPage);

    var response = service.getEvents(DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE);

    assertEquals(eventsPage, response);
  }

  @Test
  @Disabled("Temporary Disable until migrate to postgres")
  void whenGetEventsGivenRecordNonExistingInDatabase() {
    var exception =
        assertThrows(
            ContentNotFoundException.class,
            () -> service.getEvents(DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE));

    assertEquals("Content of Page EVENTS not found", exception.getMessage());
  }

  @Test
  @DisplayName(
      "Given an events page with one event, when requesting a page beyond the total, "
          + "then it should throw IllegalArgumentException")
  void shouldThrowIllegalArgumentExceptionWhenCurrentPageExceedsTotalPages() {
    var eventsPage = createEventPageTest(List.of(createEventTest()));
    var mapPage =
        new ObjectMapper().registerModule(new JavaTimeModule()).convertValue(eventsPage, Map.class);

    when(pageRepository.findById(PageType.EVENTS.getId())).thenReturn(Optional.of(mapPage));
    when(objectMapper.convertValue(anyMap(), eq(EventsPage.class))).thenReturn(eventsPage);

    assertThatThrownBy(() -> service.getEvents(DEFAULT_CURRENT_PAGE + 1, DEFAULT_PAGE_SIZE))
        .isExactlyInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "currentPage exceeds total pages. Total Pages: 1, Current Page: "
                + (DEFAULT_CURRENT_PAGE + 1));
  }

  @Test
  @DisplayName(
      "Given a stored events page that cannot be converted, when getting events, "
          + "then it should be reported as an internal error")
  void shouldWrapConversionFailureInPlatformInternalException() {
    var mapPage = Map.<String, Object>of("id", PageType.EVENTS.getId());

    when(pageRepository.findById(PageType.EVENTS.getId())).thenReturn(Optional.of(mapPage));
    when(objectMapper.convertValue(anyMap(), eq(EventsPage.class)))
        .thenThrow(new IllegalArgumentException("Invalid Json"));

    assertThatThrownBy(() -> service.getEvents(DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE))
        .isExactlyInstanceOf(PlatformInternalException.class)
        .hasMessage("Invalid Json");
  }
}
