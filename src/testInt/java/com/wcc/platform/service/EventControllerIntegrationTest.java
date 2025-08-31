package com.wcc.platform.service;

import static com.wcc.platform.domain.cms.PageType.EVENTS;
import static com.wcc.platform.domain.cms.PageType.EVENT_FILTERS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.controller.EventController;
import com.wcc.platform.domain.cms.pages.events.EventsPage;
import com.wcc.platform.repository.PageRepository;
import com.wcc.platform.utils.FileUtil;
import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class EventControllerIntegrationTest {

  private static final int DEFAULT_CURRENT_PAGE = 1;
  private static final int DEFAULT_PAGE_SIZE = 10;

  @Autowired private EventController eventController;
  @Autowired private PageRepository pageRepository;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private EventService service;

  @BeforeEach
  void deletePage() {
    pageRepository.deleteById(EVENTS.getId());
  }

  @SuppressWarnings({"unchecked", "PMD.LawOfDemeter"})
  @Test
  void testEventsApiSuccess() {
    var eventsPage = createEventTest(EVENTS.getFileName());
    pageRepository.create(objectMapper.convertValue(eventsPage, Map.class));
    var response = eventController.getEventsPage(DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE);

    assertEquals(
        eventsPage.heroSection().title(),
        Objects.requireNonNull(response.getBody()).heroSection().title());
    assertEquals(eventsPage.heroSection().subtitle(), response.getBody().heroSection().subtitle());
    assertEquals(eventsPage.contact(), response.getBody().contact());
    assertEquals(eventsPage.metadata(), response.getBody().metadata());
    assertEquals(eventsPage.data(), response.getBody().data());

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  @SuppressWarnings({"PMD.LawOfDemeter"})
  void testEventsFiltersApiSuccess() throws Exception {
    var response = eventController.getEventsFilters();

    assertEquals(HttpStatus.OK, response.getStatusCode());

    var expected = FileUtil.readFileAsString(EVENT_FILTERS.getFileName());
    var jsonResponse = objectMapper.writeValueAsString(response.getBody());

    JSONAssert.assertEquals(expected, jsonResponse, false);
  }

  @SuppressWarnings({"unchecked"})
  @Test
  void testGetEventsPage() {
    var eventsPage = createEventTest(EVENTS.getFileName());
    pageRepository.create(objectMapper.convertValue(eventsPage, Map.class));
    var result = service.getEvents(DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE);
    var expectedEventsPage = createEventTest(EVENTS.getFileName());

    assertEquals(expectedEventsPage.data(), result.data());
  }

  private EventsPage createEventTest(final String fileName) {
    try {
      String content = FileUtil.readFileAsString(fileName);
      return objectMapper.readValue(content, EventsPage.class);
    } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
      return null;
    }
  }
}
