package com.wcc.platform.integrationtests;

import static com.wcc.platform.factories.SetupFactories.OBJECT_MAPPER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.wcc.platform.controller.DefaultController;
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
class DefaultControllerIntegrationTest extends SurrealDbIntegrationTest {

  @Autowired private DefaultController controller;

  @SneakyThrows
  @Test
  void testGetLandingPageSuccess() {
    var result = controller.getLandingPage();

    assertEquals(HttpStatus.OK, result.getStatusCode());

    var expected = FileUtil.readFileAsString(ApiResourcesFile.LANDING_PAGE.getFileName());
    var jsonResponse = OBJECT_MAPPER.writeValueAsString(result.getBody());

    JSONAssert.assertEquals(expected, jsonResponse, false);
  }
}
