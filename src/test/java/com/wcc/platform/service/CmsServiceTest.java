package com.wcc.platform.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.cms.pages.CodeOfConductPage;
import com.wcc.platform.domain.cms.pages.CollaboratorPage;
import com.wcc.platform.domain.cms.pages.FooterPage;
import com.wcc.platform.domain.cms.pages.LandingPage;
import com.wcc.platform.domain.cms.pages.TeamPage;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.factories.SetupFactories;
import com.wcc.platform.repository.PageRepository;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CmsServiceTest {
  @Mock private PageRepository pageRepository;
  @Mock private ObjectMapper objectMapper;

  private CmsService service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    service = new CmsService(objectMapper, pageRepository);
  }

  @Test
  void whenGetTeamGivenInvalidJsonThenThrowsInternalException() throws IOException {
    when(objectMapper.readValue(anyString(), eq(TeamPage.class)))
        .thenThrow(new JsonProcessingException("Invalid JSON") {});

    var exception = assertThrows(PlatformInternalException.class, service::getTeam);

    assertEquals("Invalid JSON", exception.getMessage());
  }

  @Test
  void whenGetTeamGivenValidResourceThenReturnValidObjectResponse() throws IOException {
    var teamPage = SetupFactories.createTeamPageTest();
    when(objectMapper.readValue(anyString(), eq(TeamPage.class))).thenReturn(teamPage);

    var response = service.getTeam();

    assertEquals(teamPage, response);
  }

  @Test
  void whenGetFooterGivenInvalidJson() throws IOException {
    when(objectMapper.readValue(anyString(), eq(FooterPage.class)))
        .thenThrow(new JsonProcessingException("Invalid JSON") {});

    var exception = assertThrows(PlatformInternalException.class, service::getFooter);

    assertEquals("Invalid JSON", exception.getMessage());
  }

  @Test
  void whenGetFooterGivenValidJson() throws IOException {
    var footer = SetupFactories.createFooterPageTest();
    when(objectMapper.readValue(anyString(), eq(FooterPage.class))).thenReturn(footer);

    var response = service.getFooter();

    assertEquals(footer, response);
  }

  @Test
  void whenGetCollaboratorGivenInvalidJsonThenThrowsInternalException() throws IOException {
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
  void whenGetLandingPageGivenInvalidJson() throws IOException {
    when(objectMapper.readValue(anyString(), eq(LandingPage.class)))
        .thenThrow(new JsonProcessingException("Invalid JSON") {});

    var exception = assertThrows(PlatformInternalException.class, service::getLandingPage);

    assertEquals("Invalid JSON", exception.getMessage());
  }

  @Test
  void whenGetLandingPageGivenValidJsonThenReturnPage() throws IOException {
    var page =
        LandingPage.builder()
            .heroSection(SetupFactories.createPageTest("Hero"))
            .volunteerSection(SetupFactories.createPageTest("Volunteer"))
            .build();
    when(objectMapper.readValue(anyString(), eq(LandingPage.class))).thenReturn(page);

    var response = service.getLandingPage();

    assertEquals(page, response);
  }
}
