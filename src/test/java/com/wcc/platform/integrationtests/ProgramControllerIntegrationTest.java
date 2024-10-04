package com.wcc.platform.integrationtests;

import static com.wcc.platform.factories.SetupFactories.OBJECT_MAPPER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.wcc.platform.controller.ProgrammeController;
import com.wcc.platform.domain.cms.ApiResourcesFile;
import com.wcc.platform.domain.platform.ProgramType;
import com.wcc.platform.utils.FileUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ProgramControllerIntegrationTest extends SurrealDbIntegrationTest {

  @Autowired private ProgrammeController controller;

  @Test
  void testBookClubProgramSuccess() {
    var result = controller.getProgramme(ProgramType.BOOK_CLUB.toString());

    assertEquals(HttpStatus.OK, result.getStatusCode());
  }

  @SneakyThrows
  @Test
  void givenBookClubProgramApiWhenProgramTypeCorrectNameThenReturnSuccess() {
    var result = controller.getProgramme(ProgramType.BOOK_CLUB.name());

    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertNotNull(result.getBody());

    var expected = FileUtil.readFileAsString(ApiResourcesFile.PROG_BOOK_CLUB.getFileName());
    var jsonResponse = OBJECT_MAPPER.writeValueAsString(result.getBody());

    JSONAssert.assertEquals(expected, jsonResponse, false);
  }

  @Test
  void givenBookClubProgramApiWhenNotValidParamThenReturnBadRequest() {
    var result = controller.getProgramme(ProgramType.MENTORSHIP.name());

    assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
  }
}
