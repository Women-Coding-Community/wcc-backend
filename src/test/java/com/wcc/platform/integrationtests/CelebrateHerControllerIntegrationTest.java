package com.wcc.platform.integrationtests;

import static com.wcc.platform.domain.cms.PageType.CELEBRATE_HER;
import static com.wcc.platform.domain.cms.PageType.EVENTS;
import static com.wcc.platform.factories.SetupEventFactories.createEventTest;
import static com.wcc.platform.factories.SetupFactories.OBJECT_MAPPER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.controller.CelebrateHerController;
import com.wcc.platform.repository.PageRepository;
import com.wcc.platform.service.CelebrateHerService;
import com.wcc.platform.utils.FileUtil;
import java.util.Map;
import java.util.Objects;
import lombok.SneakyThrows;
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
class CelebrateHerControllerIntegrationTest extends SurrealDbIntegrationTest {

  @Autowired private CelebrateHerController celebrateHerController;
  @Autowired private PageRepository pageRepository;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private CelebrateHerService service;

  @BeforeEach
  void deletePage() {
    pageRepository.deleteById(EVENTS.getId());
  }

  @SneakyThrows
  @SuppressWarnings("unchecked")
  @Test
  void testCelebrateApiSuccess() {
    var celebrateHerPage = createEventTest(EVENTS.getFileName());
    pageRepository.create(objectMapper.convertValue(celebrateHerPage, Map.class));
    var response = celebrateHerController.getCelebrateHerPage();

    assertEquals(
        celebrateHerPage.heroSection().title(),
        Objects.requireNonNull(response.getBody()).heroSection().title());
    assertEquals(celebrateHerPage.heroSection().subtitle(), response.getBody().heroSection().subtitle());
    assertEquals(celebrateHerPage.data(), response.getBody().data());

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @SneakyThrows
  @Test
  void testCelebrateHerApiSuccess() {
    var response = celebrateHerController.getCelebrateHerPage();

    assertEquals(HttpStatus.OK, response.getStatusCode());

    var expected = FileUtil.readFileAsString(CELEBRATE_HER.getFileName());
    var jsonResponse = OBJECT_MAPPER.writeValueAsString(response.getBody());

    JSONAssert.assertEquals(expected, jsonResponse, false);
  }

  @Test
  void testGetCelebrateHerPage() {
    var eventsPage = createEventTest(CELEBRATE_HER.getFileName());
    pageRepository.create(objectMapper.convertValue(eventsPage, Map.class));
    var result = service.getCelebrateHer();
    var expectedEventsPage = createEventTest(CELEBRATE_HER.getFileName());

    assertEquals(expectedEventsPage.data(), result.data());
  }
}
