package com.wcc.platform.service;

import static com.wcc.platform.domain.cms.PageType.ABOUT_US;
import static com.wcc.platform.domain.cms.PageType.COLLABORATOR;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.repository.PageRepository;
import com.wcc.platform.repository.postgres.DefaultDatabaseSetup;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class PageServiceIntegrationTest extends DefaultDatabaseSetup {

  @Autowired private PageService service;
  @Autowired private PageRepository pageRepository;
  @Autowired private ObjectMapper objectMapper;

  @BeforeEach
  void deletePages() {
    pageRepository.deleteById(COLLABORATOR.getId());
    pageRepository.deleteById(ABOUT_US.getId());
  }

  @Test
  @DisplayName(
      "Given pageType id When create page And id is not the same as pageType "
          + "Then override content id with pageType")
  void testCreatePageIncorrectId() {
    var page = Map.of("id", "wrong-id", "name", "Test Page");

    var result = service.create(COLLABORATOR, page);

    var expected = new java.util.HashMap<>(page);
    expected.put("id", COLLABORATOR.getId());
    assertEquals(expected, result);
  }

  @Test
  @DisplayName(
      "Given pageType id When update page And id is not the same as pageType "
          + "Then override content id with pageType")
  void testUpdatePageIncorrectId() {
    var existing = Map.of("id", ABOUT_US.getId(), "name", "About Us Old");
    pageRepository.create(objectMapper.convertValue(existing, Map.class));

    var teamLikePayload = Map.of("id", "some-other-id", "name", "Updated Name");

    var result = service.update(ABOUT_US, teamLikePayload);

    var expected = new java.util.HashMap<>(teamLikePayload);
    expected.put("id", ABOUT_US.getId());
    assertEquals(expected, result);
  }
}
