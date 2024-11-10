package com.wcc.platform.integrationtests;

import static com.wcc.platform.domain.cms.ApiResourcesFile.CODE_OF_CONDUCT;
import static com.wcc.platform.domain.cms.ApiResourcesFile.COLLABORATOR;
import static com.wcc.platform.domain.cms.ApiResourcesFile.FOOTER;
import static com.wcc.platform.domain.cms.ApiResourcesFile.TEAM;
import static com.wcc.platform.factories.SetupFactories.OBJECT_MAPPER;
import static com.wcc.platform.factories.SetupFactories.createCodeOfConductPageTest;
import static com.wcc.platform.factories.SetupFactories.createCollaboratorPageTest;
import static com.wcc.platform.factories.SetupFactories.createFooterPageTest;
import static com.wcc.platform.factories.SetupFactories.createTeamPageTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.wcc.platform.domain.cms.ApiResourcesFile;
import com.wcc.platform.service.CmsService;
import com.wcc.platform.utils.FileUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CmsServiceIntegrationTest extends SurrealDbIntegrationTest {

  @Autowired private CmsService service;

  @Test
  void testGetTeamPage() {
    var result = service.getTeam();

    var expectedTeamPage = createTeamPageTest(TEAM.getFileName());

    assertEquals(expectedTeamPage.page(), result.page());
    assertEquals(expectedTeamPage.contact(), result.contact());

    assertEquals(1, result.membersByType().directors().size());
    assertEquals(1, result.membersByType().leads().size());
    assertEquals(1, result.membersByType().evangelists().size());

    assertNull(result.membersByType().directors().getFirst().getMemberTypes());
    assertNull(result.membersByType().leads().getFirst().getMemberTypes());
    assertNull(result.membersByType().evangelists().getFirst().getMemberTypes());
  }

  @Test
  void testGetFooterPageTest() {
    var result = service.getFooter();

    var expectedTeamPage = createFooterPageTest(FOOTER.getFileName());

    assertEquals(expectedTeamPage.title(), result.title());
    assertEquals(expectedTeamPage.subtitle(), result.subtitle());
    assertEquals(expectedTeamPage.description(), result.description());

    assertEquals(6, result.network().size());
    assertEquals(expectedTeamPage.link(), result.link());
  }

  @Test
  void testGetCollaboratorPage() {
    var result = service.getCollaborator();

    var expectedCollaboratorPage = createCollaboratorPageTest(COLLABORATOR.getFileName());

    assertEquals(expectedCollaboratorPage.page(), result.page());
    assertEquals(expectedCollaboratorPage.contact(), result.contact());

    assertEquals(1, result.collaborators().size());

    assertNotNull(result.collaborators().getFirst().getMemberTypes());
  }

  @Test
  void testGetCodeOfConductPage() {
    var result = service.getCodeOfConduct();
    var expectedCodeOfConductPage = createCodeOfConductPageTest(CODE_OF_CONDUCT.getFileName());

    assertEquals(expectedCodeOfConductPage, result);
  }

  @SneakyThrows
  @Test
  void testGetLandingPage() {
    var result = service.getLandingPage();

    assertNotNull(result);

    var expected = FileUtil.readFileAsString(ApiResourcesFile.LANDING_PAGE.getFileName());
    var jsonResponse = OBJECT_MAPPER.writeValueAsString(result);

    JSONAssert.assertEquals(expected, jsonResponse, false);
  }
}
