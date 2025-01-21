package com.wcc.platform.integrationtests;

import static com.wcc.platform.domain.cms.PageType.ABOUT_US;
import static com.wcc.platform.domain.cms.PageType.CODE_OF_CONDUCT;
import static com.wcc.platform.domain.cms.PageType.COLLABORATOR;
import static com.wcc.platform.domain.cms.PageType.FOOTER;
import static com.wcc.platform.domain.cms.PageType.TEAM;
import static com.wcc.platform.factories.SetupFactories.DEFAULT_CURRENT_PAGE;
import static com.wcc.platform.factories.SetupFactories.DEFAULT_PAGE_SIZE;
import static com.wcc.platform.factories.SetupFactories.createAboutUsPageTest;
import static com.wcc.platform.factories.SetupFactories.createCodeOfConductPageTest;
import static com.wcc.platform.factories.SetupFactories.createCollaboratorPageTest;
import static com.wcc.platform.factories.SetupFactories.createFooterTest;
import static com.wcc.platform.factories.SetupFactories.createTeamPageTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.cms.pages.CodeOfConductPage;
import com.wcc.platform.repository.PageRepository;
import com.wcc.platform.service.CmsService;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CmsServiceIntegrationTest extends SurrealDbIntegrationTest {

  @Autowired private CmsService service;
  @Autowired private PageRepository pageRepository;
  @Autowired private ObjectMapper objectMapper;

  @BeforeEach
  void deletePages() {
    pageRepository.deleteById(TEAM.getId());
    pageRepository.deleteById(FOOTER.getId());
    pageRepository.deleteById(ABOUT_US.getId());
  }

  @Test
  @SuppressWarnings("unchecked")
  void testGetTeamPage() {
    var teamPage = createTeamPageTest(TEAM.getFileName());
    pageRepository.create(objectMapper.convertValue(teamPage, Map.class));
    var result = service.getTeam();

    assertEquals(teamPage.page(), result.page());
    assertEquals(teamPage.contact(), result.contact());

    assertEquals(1, result.membersByType().directors().size());
    assertEquals(1, result.membersByType().leads().size());
    assertEquals(1, result.membersByType().evangelists().size());

    assertNull(result.membersByType().directors().getFirst().getMemberTypes());
    assertNull(result.membersByType().leads().getFirst().getMemberTypes());
    assertNull(result.membersByType().evangelists().getFirst().getMemberTypes());
  }

  @Test
  @SuppressWarnings("unchecked")
  void testGetFooterPageTest() {
    var footerPage = createFooterTest(FOOTER.getFileName());
    pageRepository.create(objectMapper.convertValue(footerPage, Map.class));

    var result = service.getFooter();

    assertEquals(footerPage.title(), result.title());
    assertEquals(footerPage.subtitle(), result.subtitle());
    assertEquals(footerPage.description(), result.description());

    assertEquals(6, result.network().size());
    assertEquals(footerPage.link(), result.link());
  }

  @Test
  void testGetCollaboratorPage() {
    var result = service.getCollaborator(DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE);

    var expectedCollaboratorPage = createCollaboratorPageTest(COLLABORATOR.getFileName());

    assertEquals(expectedCollaboratorPage.page(), result.page());
    assertEquals(expectedCollaboratorPage.contact(), result.contact());

    assertEquals(1, result.collaborators().size());

    assertNotNull(result.collaborators().getFirst().getMemberTypes());
  }

  @Test
  void testGetCodeOfConductPage() {
    CodeOfConductPage codeOfConductPage =
        createCodeOfConductPageTest(CODE_OF_CONDUCT.getFileName());
    pageRepository.create(objectMapper.convertValue(codeOfConductPage, Map.class));

    var result = service.getCodeOfConduct();

    assertEquals(codeOfConductPage.page(), result.page());
    assertEquals(codeOfConductPage.items().size(), result.items().size());
  }

  @Test
  void testGetAboutUsPage() {
    var aboutUsPage = createAboutUsPageTest(ABOUT_US.getFileName());
    pageRepository.create(objectMapper.convertValue(aboutUsPage, Map.class));
    var result = service.getAboutUs();

    assertEquals(aboutUsPage.heroSection(), result.heroSection());
    assertEquals(aboutUsPage.items(), result.items());
    assertEquals(aboutUsPage.contact(), result.contact());
  }
}
