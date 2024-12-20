package com.wcc.platform.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.cms.PageType;
import com.wcc.platform.domain.cms.pages.AboutUsPage;
import com.wcc.platform.domain.cms.pages.CodeOfConductPage;
import com.wcc.platform.domain.cms.pages.CollaboratorPage;
import com.wcc.platform.domain.cms.pages.LandingPage;
import com.wcc.platform.domain.cms.pages.TeamPage;
import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.factories.SetupFactories;
import com.wcc.platform.repository.PageRepository;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CmsServiceTest {

  private final LandingPage landingPage =
      LandingPage.builder()
          .heroSection(SetupFactories.createPageTest("Hero"))
          .volunteerSection(SetupFactories.createPageTest("Volunteer"))
          .build();
  @Mock private PageRepository pageRepository;
  @Mock private ObjectMapper objectMapper;
  private CmsService service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    service = new CmsService(objectMapper, pageRepository);
  }

  @Test
  void whenGetTeamGivenNotOnDatabaseThenThrowsException() {

    var exception = assertThrows(ContentNotFoundException.class, service::getTeam);

    assertEquals("Content of Page TEAM not found", exception.getMessage());
  }

  @Test
  @SuppressWarnings("unchecked")
  void whenGetTeamGivenRecordExistOnDatabaseThenReturnValidResponse() {
    var teamPage = SetupFactories.createTeamPageTest();
    var mapPage = new ObjectMapper().convertValue(teamPage, Map.class);

    when(pageRepository.findById(PageType.TEAM.getPageId())).thenReturn(Optional.of(mapPage));
    when(objectMapper.convertValue(anyMap(), eq(TeamPage.class))).thenReturn(teamPage);

    var response = service.getTeam();

    assertEquals(teamPage, response);
  }

  @Test
  void whenGetCollaboratorGivenNotFoundThenThrowsInternalException() throws IOException {
    when(objectMapper.readValue(anyString(), eq(CollaboratorPage.class)))
        .thenThrow(new JsonProcessingException("Invalid JSON") {});

    var exception = assertThrows(PlatformInternalException.class, service::getCollaborator);

    assertEquals("Invalid JSON", exception.getMessage());
  }

  @Test
  void whenGetCollaboratorGivenValidResourceThenReturnValidObjectResponse() throws IOException {
    var collaboratorPage = SetupFactories.createCollaboratorPageTest();
    when(objectMapper.readValue(anyString(), eq(CollaboratorPage.class)))
        .thenReturn(collaboratorPage);

    var response = service.getCollaborator();

    assertEquals(collaboratorPage, response);
  }

  @Test
  void whenGetCodeOfConductGivenInvalidJson() throws IOException {
    when(objectMapper.readValue(anyString(), eq(CodeOfConductPage.class)))
        .thenThrow(new JsonProcessingException("Invalid JSON") {});

    var exception = assertThrows(PlatformInternalException.class, service::getCodeOfConduct);

    assertEquals("Invalid JSON", exception.getMessage());
  }

  @Test
  void whenGetCodeOfConductGivenValidJson() throws IOException {
    var codeOfConductPage = SetupFactories.createCodeOfConductPageTest();
    when(objectMapper.readValue(anyString(), eq(CodeOfConductPage.class)))
        .thenReturn(codeOfConductPage);

    var response = service.getCodeOfConduct();

    assertEquals(codeOfConductPage, response);
  }

  @Test
  void whenGetLandingPageGivenNotStoredInDatabaseThenThrowsException() {
    var exception = assertThrows(ContentNotFoundException.class, service::getLandingPage);

    assertEquals("Content of Page LANDING_PAGE not found", exception.getMessage());
  }

  @Test
  @SuppressWarnings("unchecked")
  void whenGetLandingPageGivenRecordExistOnDatabaseThenReturnPage() {
    var mapPage = new ObjectMapper().convertValue(landingPage, Map.class);

    when(pageRepository.findById(PageType.LANDING_PAGE.getPageId()))
        .thenReturn(Optional.of(mapPage));
    when(objectMapper.convertValue(anyMap(), eq(LandingPage.class))).thenReturn(landingPage);

    var response = service.getLandingPage();

    assertEquals(landingPage, response);
  }

  @Test
  @SuppressWarnings("unchecked")
  void whenGetLandingPageGivenRecordExistOnDatabaseAndHasExceptionToConvertThenThrowsException() {
    var mapPage = new ObjectMapper().convertValue(landingPage, Map.class);

    when(pageRepository.findById(PageType.LANDING_PAGE.getPageId()))
        .thenReturn(Optional.of(mapPage));
    when(objectMapper.convertValue(anyMap(), eq(LandingPage.class)))
        .thenThrow(new IllegalArgumentException());

    assertThrows(PlatformInternalException.class, service::getLandingPage);
  }

  @Test
  void whenGetAboutUsPageGivenInvalidJson() throws IOException {
    when(objectMapper.readValue(anyString(), eq(AboutUsPage.class)))
        .thenThrow(new JsonProcessingException("Invalid JSON") {});

    var exception = assertThrows(PlatformInternalException.class, service::getAboutUs);

    assertEquals("Invalid JSON", exception.getMessage());
  }

  @Test
  void whenGetAboutUsPageGivenValidJson() throws IOException {
    var aboutUsPage = SetupFactories.createAboutUsPageTest();
    when(objectMapper.readValue(anyString(), eq(AboutUsPage.class))).thenReturn(aboutUsPage);

    var response = service.getAboutUs();

    assertEquals(aboutUsPage, response);
  }
}
