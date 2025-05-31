// package com.wcc.platform.integrationtests;
//
// import static com.wcc.platform.domain.cms.PageType.PROG_BOOK_CLUB;
// import static com.wcc.platform.domain.platform.ProgramType.BOOK_CLUB;
// import static com.wcc.platform.factories.SetupFactories.OBJECT_MAPPER;
// import static com.wcc.platform.factories.SetupProgrammeFactories.createProgrammePageTest;
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertThrows;
//
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.wcc.platform.controller.ProgrammeController;
// import com.wcc.platform.domain.exceptions.ContentNotFoundException;
// import com.wcc.platform.domain.platform.ProgramType;
// import com.wcc.platform.repository.PageRepository;
// import com.wcc.platform.utils.FileUtil;
// import java.util.Map;
// import lombok.SneakyThrows;
// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.skyscreamer.jsonassert.JSONAssert;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
// import org.springframework.http.HttpStatus;
// import org.springframework.test.context.ActiveProfiles;
//
// @ActiveProfiles("test")
// @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
// class ProgramControllerIntegrationTest extends SurrealDbIntegrationTest {
//
//  @Autowired private ProgrammeController controller;
//  @Autowired private PageRepository pageRepository;
//  @Autowired private ObjectMapper objectMapper;
//
//  @AfterEach
//  void deletePage() {
//    pageRepository.deleteById(PROG_BOOK_CLUB.getId());
//  }
//
//  @BeforeEach
//  void setupDatabase() {
//    var page = createProgrammePageTest(PROG_BOOK_CLUB.getFileName());
//    pageRepository.create(objectMapper.convertValue(page, Map.class));
//  }
//
//  @Test
//  void testBookClubProgramSuccess() {
//    var result = controller.getProgramme(BOOK_CLUB);
//
//    assertEquals(HttpStatus.OK, result.getStatusCode());
//  }
//
//  @Test
//  @SneakyThrows
//  void givenBookClubProgramApiWhenProgramTypeCorrectNameThenReturnSuccess() {
//    var result = controller.getProgramme(BOOK_CLUB);
//
//    assertEquals(HttpStatus.OK, result.getStatusCode());
//    assertNotNull(result.getBody());
//
//    var expected = FileUtil.readFileAsString(PROG_BOOK_CLUB.getFileName());
//    var jsonResponse = OBJECT_MAPPER.writeValueAsString(result.getBody());
//
//    JSONAssert.assertEquals(expected, jsonResponse, false);
//  }
//
//  @Test
//  void givenProgramMachineLearningWhenNotCreatedInDbThenReturnNotFound() {
//    assertThrows(
//        ContentNotFoundException.class,
//        () -> controller.getProgramme(ProgramType.MACHINE_LEARNING));
//  }
// }
