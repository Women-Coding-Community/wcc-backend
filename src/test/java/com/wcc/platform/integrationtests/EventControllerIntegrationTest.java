package com.wcc.platform.integrationtests;

import static com.wcc.platform.factories.SetupFactories.OBJECT_MAPPER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.wcc.platform.controller.EventController;
import com.wcc.platform.domain.cms.ApiResourcesFile;
import com.wcc.platform.utils.FileUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class EventControllerIntegrationTest {

  @Autowired private EventController eventController;

  @SneakyThrows
  @Test
  void testEventsAPISuccess() {
    var response = eventController.getEventsPage();

    assertEquals(HttpStatus.OK, response.getStatusCode());

    var expected = FileUtil.readFileAsString(ApiResourcesFile.EVENTS.getFileName());
    var jsonResponse = OBJECT_MAPPER.writeValueAsString(response.getBody());

    JSONAssert.assertEquals(expected, jsonResponse, false);
  }

  @SneakyThrows
  @Test
  void testEventsFiltersAPISuccess() {
    var response = eventController.getEventsFilters();

    assertEquals(HttpStatus.OK, response.getStatusCode());

    var expected = FileUtil.readFileAsString(ApiResourcesFile.EVENT_FILTERS.getFileName());
    var jsonResponse = OBJECT_MAPPER.writeValueAsString(response.getBody());

    JSONAssert.assertEquals(expected, jsonResponse, false);
  }
}
