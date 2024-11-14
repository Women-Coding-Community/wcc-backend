package com.wcc.platform.integrationtests;

import static com.wcc.platform.domain.cms.PageType.EVENTS;
import static com.wcc.platform.domain.cms.PageType.EVENT_FILTERS;
import static com.wcc.platform.factories.SetupEventFactories.createEventTest;
import static com.wcc.platform.factories.SetupFactories.OBJECT_MAPPER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.wcc.platform.controller.EventController;
import com.wcc.platform.service.EventService;
import com.wcc.platform.utils.FileUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class EventControllerIntegrationTest extends SurrealDbIntegrationTest {

  @Autowired private EventController eventController;

  @Autowired private EventService service;

  @SneakyThrows
  @Test
  void testEventsApiSuccess() {
    var response = eventController.getEventsPage();

    assertEquals(HttpStatus.OK, response.getStatusCode());

    var expected = FileUtil.readFileAsString(EVENTS.getFileName());
    var jsonResponse = OBJECT_MAPPER.writeValueAsString(response.getBody());

    JSONAssert.assertEquals(expected, jsonResponse, false);
  }

  @SneakyThrows
  @Test
  void testEventsFiltersApiSuccess() {
    var response = eventController.getEventsFilters();

    assertEquals(HttpStatus.OK, response.getStatusCode());

    var expected = FileUtil.readFileAsString(EVENT_FILTERS.getFileName());
    var jsonResponse = OBJECT_MAPPER.writeValueAsString(response.getBody());

    JSONAssert.assertEquals(expected, jsonResponse, false);
  }

  @Test
  void testGetEventsPage() {
    var result = service.getEvents();
    var expectedEventsPage = createEventTest(EVENTS.getFileName());

    assertEquals(expectedEventsPage, result);
  }
}
