package com.wcc.platform.integrationtests;

import static com.wcc.platform.domain.cms.PageType.CELEBRATE_HER;
import static com.wcc.platform.factories.SetupCelebrateHerFactories.createCelebrateHerPageTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.controller.CelebrateHerController;
import com.wcc.platform.repository.PageRepository;
import com.wcc.platform.service.CelebrateHerService;
import java.util.Map;
import java.util.Objects;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
    pageRepository.deleteById(CELEBRATE_HER.getId());
  }

  @SneakyThrows
  @SuppressWarnings("unchecked")
  @Test
  void testCelebrateApiSuccess() {
    var celebrateHerPage = createCelebrateHerPageTest(CELEBRATE_HER.getFileName());
    pageRepository.create(objectMapper.convertValue(celebrateHerPage, Map.class));
    var response = celebrateHerController.getCelebrateHerPage();

    assertEquals(
        celebrateHerPage.heroSection().title(),
        Objects.requireNonNull(response.getBody()).heroSection().title());
    assertEquals(
        celebrateHerPage.heroSection().subtitle(), response.getBody().heroSection().subtitle());
    assertEquals(celebrateHerPage.data(), response.getBody().data());

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void testGetCelebrateHerPage() {
    var celebrateHerPages = createCelebrateHerPageTest(CELEBRATE_HER.getFileName());
    pageRepository.create(objectMapper.convertValue(celebrateHerPages, Map.class));
    var result = service.getCelebrateHer();
    var expectedCelebrateHerPages = createCelebrateHerPageTest(CELEBRATE_HER.getFileName());

    assertEquals(expectedCelebrateHerPages.data(), result.data());
  }
}
