package com.wcc.platform.service;

import static com.wcc.platform.domain.cms.ApiResourcesFile.*;
import static com.wcc.platform.factories.TestFactories.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CmsServiceIntegrationTest {

  @Autowired private CmsService service;

  @Test
  void getTeamPageTest() {
    var result = service.getTeam();

    var expectedTeamPage = createTeamPageTest(TEAM.getFileName());

    assertEquals(expectedTeamPage.page(), result.page());
    assertEquals(expectedTeamPage.contact(), result.contact());

    assertEquals(1, result.membersByType().directors().size());
    assertEquals(1, result.membersByType().leads().size());
    assertEquals(1, result.membersByType().evangelists().size());

    assertNull(result.membersByType().directors().get(0).getMemberType());
    assertNull(result.membersByType().leads().get(0).getMemberType());
    assertNull(result.membersByType().evangelists().get(0).getMemberType());
  }

  @Test
  void getFooterPageTest() {
    var result = service.getFooter();

    var expectedTeamPage = createFooterPageTest(FOOTER.getFileName());

    assertEquals(expectedTeamPage.title(), result.title());
    assertEquals(expectedTeamPage.subtitle(), result.subtitle());
    assertEquals(expectedTeamPage.description(), result.description());

    assertEquals(6, result.network().size());
    assertEquals(expectedTeamPage.link(), result.link());
  }

  @Test
  void getCollaboratorPageTest() {
    var result = service.getCollaborator();

    var expectedCollaboratorPage = createCollaboratorPageTest(COLLABORATOR.getFileName());

    assertEquals(expectedCollaboratorPage.page(), result.page());
    assertEquals(expectedCollaboratorPage.contact(), result.contact());

    assertEquals(1, result.collaborators().size());

    assertNotNull(result.collaborators().get(0).getMemberType());
  }

  @Test
  void getCodeOfConductPageTest() {
    var result = service.getCodeOfConduct();

    var expectedCodeOfConductPage = createCodeOfConductPageTest(CODE_OF_CONDUCT.getFileName());

    assertEquals(expectedCodeOfConductPage, result);
  }
}
