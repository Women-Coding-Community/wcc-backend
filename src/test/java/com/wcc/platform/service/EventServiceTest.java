package com.wcc.platform.service;

import static com.wcc.platform.factories.SetupEventFactories.DEFAULT_CURRENT_PAGE;
import static com.wcc.platform.factories.SetupEventFactories.DEFAULT_PAGE_SIZE;
import static com.wcc.platform.factories.SetupEventFactories.createEventPageTest;
import static com.wcc.platform.factories.SetupEventFactories.createEventTest;
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
import com.wcc.platform.repository.PageRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class EventServiceTest {

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
  void whenGetEventsGivenRecordNonExistingInDatabase() {
    var exception = assertThrows(
        ContentNotFoundException.class,
        () -> service.getEvents(DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE)
    );

    assertEquals("Content of Page EVENTS not found", exception.getMessage());

  }
  
}
