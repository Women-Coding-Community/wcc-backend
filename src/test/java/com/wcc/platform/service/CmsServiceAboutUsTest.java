package com.wcc.platform.service;

import static com.wcc.platform.factories.SetupFactories.DEFAULT_CURRENT_PAGE;
import static com.wcc.platform.factories.SetupFactories.DEFAULT_PAGE_SIZE;
import static com.wcc.platform.factories.SetupPagesFactories.createCodeOfConductPageTest;
import static com.wcc.platform.factories.SetupPagesFactories.createCollaboratorPageTest;
import static com.wcc.platform.factories.SetupPagesFactories.createPartnersPageTest;
import static com.wcc.platform.factories.SetupPagesFactories.createTeamPageTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.cms.PageType;
import com.wcc.platform.domain.cms.pages.CollaboratorPage;
import com.wcc.platform.domain.cms.pages.TeamPage;
import com.wcc.platform.domain.cms.pages.aboutus.AboutUsPage;
import com.wcc.platform.domain.cms.pages.aboutus.CelebrateHerPage;
import com.wcc.platform.domain.cms.pages.aboutus.CodeOfConductPage;
import com.wcc.platform.domain.cms.pages.aboutus.PartnersPage;
import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.factories.SetupPagesFactories;
import com.wcc.platform.repository.PageRepository;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@SuppressWarnings("unchecked")
class CmsServiceAboutUsTest {

  @Mock private PageRepository pageRepository;
  @Mock private ObjectMapper objectMapper;
  private CmsAboutUsService service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    service = new CmsAboutUsService(objectMapper, pageRepository);
  }

  @Test
  void whenGetTeamGivenNotOnDatabaseThenThrowsException() {
    var exception = assertThrows(ContentNotFoundException.class, service::getTeam);

    assertEquals("Content of Page TEAM not found", exception.getMessage());
  }

  @Test
  void whenGetTeamGivenRecordExistOnDatabaseThenReturnValidResponse() {
    var teamPage = createTeamPageTest();
    var mapPage = new ObjectMapper().convertValue(teamPage, Map.class);

    when(pageRepository.findById(PageType.TEAM.getId())).thenReturn(Optional.of(mapPage));
    when(objectMapper.convertValue(anyMap(), eq(TeamPage.class))).thenReturn(teamPage);

    var response = service.getTeam();

    assertEquals(teamPage, response);
  }

  @Test
  void whenGetCollaboratorNotInDatabase() {
    var exception =
        assertThrows(
            ContentNotFoundException.class,
            () -> service.getCollaborator(DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE));

    assertEquals("Content of Page COLLABORATOR not found", exception.getMessage());
  }

  @Test
  void whenGetCollaboratorInDatabase() {
    var collaboratorPage = createCollaboratorPageTest();
    var mapPage = new ObjectMapper().convertValue(collaboratorPage, Map.class);

    when(pageRepository.findById(PageType.COLLABORATOR.getId())).thenReturn(Optional.of(mapPage));
    when(objectMapper.convertValue(anyMap(), eq(CollaboratorPage.class)))
        .thenReturn(collaboratorPage);

    var response = service.getCollaborator(DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE);

    assertEquals(collaboratorPage, response);
  }

  @Test
  void whenGetPartnersNotInDatabase() {
    var exception = assertThrows(ContentNotFoundException.class, service::getPartners);

    assertEquals("Content of Page PARTNERS not found", exception.getMessage());
  }

  @Test
  void whenGetPartnersInDatabase() {
    var partnersPage = createPartnersPageTest();
    var mapPage = new ObjectMapper().convertValue(partnersPage, Map.class);

    when(pageRepository.findById(PageType.PARTNERS.getId())).thenReturn(Optional.of(mapPage));
    when(objectMapper.convertValue(anyMap(), eq(PartnersPage.class))).thenReturn(partnersPage);

    var response = service.getPartners();

    assertEquals(partnersPage, response);
  }

  @Test
  void whenGetCodeOfConductNotInDatabase() {
    var exception = assertThrows(ContentNotFoundException.class, service::getCodeOfConduct);

    assertEquals("Content of Page CODE_OF_CONDUCT not found", exception.getMessage());
  }

  @Test
  void whenGetCodeOfConductInDatabase() {
    var codeOfConductPage = createCodeOfConductPageTest();
    var mapPage = new ObjectMapper().convertValue(codeOfConductPage, Map.class);

    when(pageRepository.findById(PageType.CODE_OF_CONDUCT.getId()))
        .thenReturn(Optional.of(mapPage));
    when(objectMapper.convertValue(anyMap(), eq(CodeOfConductPage.class)))
        .thenReturn(codeOfConductPage);

    var response = service.getCodeOfConduct();

    assertEquals(codeOfConductPage, response);
  }

  @Test
  void whenGetAboutUsPageGivenNotStoredInDatabaseThenThrowsException() {
    var exception = assertThrows(ContentNotFoundException.class, service::getAboutUs);

    assertEquals("Content of Page ABOUT_US not found", exception.getMessage());
  }

  @Test
  void whenGetAboutUsPageGivenExistOnDatabaseThenReturnValidResponse() {
    var aboutUsPage = SetupPagesFactories.createAboutUsPageTest();
    var mapPage = new ObjectMapper().convertValue(aboutUsPage, Map.class);

    when(pageRepository.findById(PageType.ABOUT_US.getId())).thenReturn(Optional.of(mapPage));
    when(objectMapper.convertValue(anyMap(), eq(AboutUsPage.class))).thenReturn(aboutUsPage);

    var response = service.getAboutUs();

    assertEquals(aboutUsPage, response);
  }

  @Test
  void whenGetCelebrateHerPageGivenNotStoredInDatabaseThenThrowsException() {
    var exception = assertThrows(ContentNotFoundException.class, service::getCelebrateHer);

    assertEquals("Content of Page CELEBRATE_HER not found", exception.getMessage());
  }

  @Test
  void whenGetCelebrateHerPageGivenExistOnDatabaseThenReturnValidResponse() {
    var celebrateHerPage = SetupPagesFactories.createCelebrateHerPageTest();
    var mapPage = new ObjectMapper().convertValue(celebrateHerPage, Map.class);

    when(pageRepository.findById(PageType.CELEBRATE_HER.getId())).thenReturn(Optional.of(mapPage));
    when(objectMapper.convertValue(anyMap(), eq(CelebrateHerPage.class)))
        .thenReturn(celebrateHerPage);

    var response = service.getCelebrateHer();

    assertEquals(celebrateHerPage, response);
  }
}
