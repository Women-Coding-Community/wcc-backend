package com.wcc.platform.service;

import static com.wcc.platform.domain.platform.type.ProgramType.BOOK_CLUB;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.controller.ProgrammeController;
import com.wcc.platform.domain.cms.pages.programme.ProgrammePage;
import com.wcc.platform.repository.PageRepository;
import com.wcc.platform.repository.postgres.DefaultDatabaseSetup;
import com.wcc.platform.utils.FileUtil;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ProgramControllerIntegrationTest extends DefaultDatabaseSetup {

  private static final String PAGE_JSON = "init-data/bookClubPage.json";

  @Autowired private ProgrammeController controller;
  @Autowired private PageRepository pageRepository;
  @Autowired private ObjectMapper objectMapper;

  @AfterEach
  void deletePage() {
    pageRepository.deleteById(BOOK_CLUB.toPageId());
  }

  @BeforeEach
  @SuppressWarnings("unchecked")
  void setupDatabase() throws JsonProcessingException {
    deletePage();
    var page = createProgrammePageTest();
    pageRepository.create(objectMapper.convertValue(page, Map.class));
  }

  @Test
  void testBookClubProgramSuccess() {
    var result = controller.getProgramme(BOOK_CLUB);

    assertEquals(HttpStatus.OK, result.getStatusCode());
  }

  private ProgrammePage createProgrammePageTest() throws JsonProcessingException {
    String content = FileUtil.readFileAsString(PAGE_JSON);
    return objectMapper.readValue(content, ProgrammePage.class);
  }
}
