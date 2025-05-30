package com.wcc.platform.service;

import static com.wcc.platform.factories.SetupFactories.DEFAULT_CURRENT_PAGE;
import static com.wcc.platform.factories.SetupFactories.DEFAULT_PAGE_SIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wcc.platform.domain.cms.PageType;
import com.wcc.platform.domain.cms.pages.CollaboratorPage;
import com.wcc.platform.domain.cms.pages.LandingPage;
import com.wcc.platform.domain.cms.pages.TeamPage;
import com.wcc.platform.domain.cms.pages.aboutus.AboutUsPage;
import com.wcc.platform.domain.cms.pages.aboutus.CelebrateHerPage;
import com.wcc.platform.domain.cms.pages.aboutus.CodeOfConductPage;
import com.wcc.platform.domain.cms.pages.aboutus.PartnersPage;
import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.factories.SetupFactories;
import com.wcc.platform.factories.SetupMentorshipFactories;
import com.wcc.platform.repository.PageRepository;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CmsServiceTest {
  private final LandingPage landingPage =
      LandingPage.builder()
          .id(PageType.LANDING_PAGE.getId())
          .heroSection(SetupFactories.createHeroSectionTest())
          .fullBannerSection(SetupFactories.createCommonSectionTest("Page banner section"))
          .feedbackSection(SetupMentorshipFactories.createFeedbackSectionTest())
          .volunteerSection(SetupFactories.createCommonSectionTest("Volunteer"))
          .partners(SetupFactories.createListSectionPartnerTest())
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
  @Disabled("Temporary Disable until migrate to postgres")
  void whenGetTeamGivenNotOnDatabaseThenThrowsException() {

    var exception = assertThrows(ContentNotFoundException.class, service::getTeam);

    assertEquals("Content of Page TEAM not found", exception.getMessage());
  }

  @Test
  @SuppressWarnings("unchecked")
  void whenGetTeamGivenRecordExistOnDatabaseThenReturnValidResponse() {
    var teamPage = SetupFactories.createTeamPageTest();
    var mapPage = new ObjectMapper().convertValue(teamPage, Map.class);

    when(pageRepository.findById(PageType.TEAM.getId())).thenReturn(Optional.of(mapPage));
    when(objectMapper.convertValue(anyMap(), eq(TeamPage.class))).thenReturn(teamPage);

    var response = service.getTeam();

    assertEquals(teamPage, response);
  }

  @Test
  @Disabled("Temporary Disable until migrate to postgres")
  void whenGetCollaboratorNotInDatabase() {
    var exception =
        assertThrows(
            ContentNotFoundException.class,
            () -> service.getCollaborator(DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE));

    assertEquals("Content of Page COLLABORATOR not found", exception.getMessage());
  }

  @Test
  void whenGetCollaboratorInDatabase() {
    var collaboratorPage = SetupFactories.createCollaboratorPageTest();
    var mapPage = new ObjectMapper().convertValue(collaboratorPage, Map.class);

    when(pageRepository.findById(PageType.COLLABORATOR.getId())).thenReturn(Optional.of(mapPage));
    when(objectMapper.convertValue(anyMap(), eq(CollaboratorPage.class)))
        .thenReturn(collaboratorPage);

    var response = service.getCollaborator(DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE);

    assertEquals(collaboratorPage, response);
  }

  @Test
  @Disabled("Temporary Disable until migrate to postgres")
  void whenGetPartnersNotInDatabase() {
    var exception = assertThrows(ContentNotFoundException.class, service::getPartners);

    assertEquals("Content of Page PARTNERS not found", exception.getMessage());
  }

  @Test
  void whenGetPartnersInDatabase() {
    var partnersPage = SetupFactories.createPartnersPageTest();
    var mapPage = new ObjectMapper().convertValue(partnersPage, Map.class);

    when(pageRepository.findById(PageType.PARTNERS.getId())).thenReturn(Optional.of(mapPage));
    when(objectMapper.convertValue(anyMap(), eq(PartnersPage.class))).thenReturn(partnersPage);

    var response = service.getPartners();

    assertEquals(partnersPage, response);
  }

  @Test
  @Disabled("Temporary Disable until migrate to postgres")
  void whenGetCodeOfConductNotInDatabase() {
    var exception = assertThrows(ContentNotFoundException.class, service::getCodeOfConduct);

    assertEquals("Content of Page CODE_OF_CONDUCT not found", exception.getMessage());
  }

  @Test
  void whenGetCodeOfConductInDatabase() {
    var codeOfConductPage = SetupFactories.createCodeOfConductPageTest();
    var mapPage = new ObjectMapper().convertValue(codeOfConductPage, Map.class);

    when(pageRepository.findById(PageType.CODE_OF_CONDUCT.getId()))
        .thenReturn(Optional.of(mapPage));
    when(objectMapper.convertValue(anyMap(), eq(CodeOfConductPage.class)))
        .thenReturn(codeOfConductPage);

    var response = service.getCodeOfConduct();

    assertEquals(codeOfConductPage, response);
  }

  @Test
  @Disabled("Temporary Disable until migrate to postgres")
  void whenGetLandingPageGivenNotStoredInDatabaseThenThrowsException() {
    var exception = assertThrows(ContentNotFoundException.class, service::getLandingPage);

    assertEquals("Content of Page LANDING_PAGE not found", exception.getMessage());
  }

  @Test
  @SuppressWarnings("unchecked")
  void whenGetLandingPageGivenRecordExistOnDatabaseThenReturnPage() {
    var mapPage =
        new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .convertValue(landingPage, Map.class);

    when(pageRepository.findById(PageType.LANDING_PAGE.getId())).thenReturn(Optional.of(mapPage));
    when(objectMapper.convertValue(anyMap(), eq(LandingPage.class))).thenReturn(landingPage);

    var response = service.getLandingPage();

    assertEquals(landingPage, response);
  }

  @Test
  @SuppressWarnings("unchecked")
  void whenGetLandingPageGivenRecordExistOnDatabaseAndHasExceptionToConvertThenThrowsException() {
    var mapPage =
        new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .convertValue(landingPage, Map.class);

    when(pageRepository.findById(PageType.LANDING_PAGE.getId())).thenReturn(Optional.of(mapPage));
    when(objectMapper.convertValue(anyMap(), eq(LandingPage.class)))
        .thenThrow(new IllegalArgumentException());

    assertThrows(PlatformInternalException.class, service::getLandingPage);
  }

  @Test
  @Disabled("Temporary Disable until migrate to postgres")
  void whenGetAboutUsPageGivenNotStoredInDatabaseThenThrowsException() {
    var exception = assertThrows(ContentNotFoundException.class, service::getAboutUs);

    assertEquals("Content of Page ABOUT_US not found", exception.getMessage());
  }

  @SuppressWarnings("unchecked")
  @Test
  void whenGetAboutUsPageGivenExistOnDatabaseThenReturnValidResponse() {
    var aboutUsPage = SetupFactories.createAboutUsPageTest();
    var mapPage = new ObjectMapper().convertValue(aboutUsPage, Map.class);

    when(pageRepository.findById(PageType.ABOUT_US.getId())).thenReturn(Optional.of(mapPage));
    when(objectMapper.convertValue(anyMap(), eq(AboutUsPage.class))).thenReturn(aboutUsPage);

    var response = service.getAboutUs();

    assertEquals(aboutUsPage, response);
  }

  @Test
  @Disabled("Temporary Disable until migrate to postgres")
  void whenGetCelebrateHerPageGivenNotStoredInDatabaseThenThrowsException() {
    var exception = assertThrows(ContentNotFoundException.class, service::getCelebrateHer);

    assertEquals("Content of Page CELEBRATE_HER not found", exception.getMessage());
  }

  @SuppressWarnings("unchecked")
  @Test
  void whenGetCelebrateHerPageGivenExistOnDatabaseThenReturnValidResponse() {
    var celebrateHerPage = SetupFactories.createCelebrateHerPageTest();
    var mapPage = new ObjectMapper().convertValue(celebrateHerPage, Map.class);

    when(pageRepository.findById(PageType.CELEBRATE_HER.getId())).thenReturn(Optional.of(mapPage));
    when(objectMapper.convertValue(anyMap(), eq(CelebrateHerPage.class)))
        .thenReturn(celebrateHerPage);

    var response = service.getCelebrateHer();

    assertEquals(celebrateHerPage, response);
  }
}
