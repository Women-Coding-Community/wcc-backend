package com.wcc.platform.service;

import static com.wcc.platform.domain.cms.PageType.PROG_BOOK_CLUB;
import static com.wcc.platform.domain.platform.type.ProgramType.BOOK_CLUB;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.controller.ProgrammeController;
import com.wcc.platform.domain.cms.pages.programme.ProgrammePage;
import com.wcc.platform.repository.PageRepository;
import com.wcc.platform.utils.FileUtil;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
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
class ProgramControllerIntegrationTest {

  @Autowired private ProgrammeController controller;
  @Autowired private PageRepository pageRepository;
  @Autowired private ObjectMapper objectMapper;

  @AfterEach
  void deletePage() {
    pageRepository.deleteById(PROG_BOOK_CLUB.getId());
  }

  @BeforeEach
  void setupDatabase() {
    var page = createProgrammePageTest(PROG_BOOK_CLUB.getFileName());
    pageRepository.create(objectMapper.convertValue(page, Map.class));
  }

  @Test
  void testBookClubProgramSuccess() {
    var result = controller.getProgramme(BOOK_CLUB);

    assertEquals(HttpStatus.OK, result.getStatusCode());
  }

  @Test
  void givenBookClubProgramApiWhenProgramTypeCorrectNameThenReturnSuccess() throws Exception {
    var result = controller.getProgramme(BOOK_CLUB);

    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertNotNull(result.getBody());

    var expected = FileUtil.readFileAsString(PROG_BOOK_CLUB.getFileName());
    var jsonResponse = objectMapper.writeValueAsString(result.getBody());

    JSONAssert.assertEquals(expected, jsonResponse, false);
  }
  
  private ProgrammePage createProgrammePageTest(String fileName) {
    try {
      String content = FileUtil.readFileAsString(fileName);
      return objectMapper.readValue(content, ProgrammePage.class);
    } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
      // In integration tests we expect the JSON to exist; if not, rethrow to fail fast.
      throw new RuntimeException(e);
    }
  }
}
