// package com.wcc.platform.integrationtests;
//
// import static com.wcc.platform.domain.cms.PageType.ABOUT_US;
// import static com.wcc.platform.domain.cms.PageType.CELEBRATE_HER;
// import static com.wcc.platform.domain.cms.PageType.CODE_OF_CONDUCT;
// import static com.wcc.platform.domain.cms.PageType.COLLABORATOR;
// import static com.wcc.platform.domain.cms.PageType.FOOTER;
// import static com.wcc.platform.domain.cms.PageType.LANDING_PAGE;
// import static com.wcc.platform.domain.cms.PageType.PARTNERS;
// import static com.wcc.platform.domain.cms.PageType.TEAM;
// import static com.wcc.platform.factories.SetupFactories.DEFAULT_CURRENT_PAGE;
// import static com.wcc.platform.factories.SetupFactories.DEFAULT_PAGE_SIZE;
// import static com.wcc.platform.factories.SetupFactories.createAboutUsPageTest;
// import static com.wcc.platform.factories.SetupFactories.createCelebrateHerPageTest;
// import static com.wcc.platform.factories.SetupFactories.createCodeOfConductPageTest;
// import static com.wcc.platform.factories.SetupFactories.createCollaboratorPageTest;
// import static com.wcc.platform.factories.SetupFactories.createFooterTest;
// import static com.wcc.platform.factories.SetupFactories.createLandingPageTest;
// import static com.wcc.platform.factories.SetupFactories.createPartnersPageTest;
// import static com.wcc.platform.factories.SetupFactories.createTeamPageTest;
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertNull;
//
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.wcc.platform.domain.cms.pages.CollaboratorPage;
// import com.wcc.platform.domain.cms.pages.aboutus.CodeOfConductPage;
// import com.wcc.platform.repository.PageRepository;
// import com.wcc.platform.service.CmsService;
// import java.util.Map;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
// import org.springframework.test.context.ActiveProfiles;
//
// @ActiveProfiles("test")
// @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
// class CmsServiceIntegrationTest extends x {
//
//  @Autowired private CmsService service;
//  @Autowired private PageRepository pageRepository;
//  @Autowired private ObjectMapper objectMapper;
//
//  @BeforeEach
//  void deletePages() {
//    pageRepository.deleteById(TEAM.getId());
//    pageRepository.deleteById(FOOTER.getId());
//    pageRepository.deleteById(ABOUT_US.getId());
//    pageRepository.deleteById(COLLABORATOR.getId());
//    pageRepository.deleteById(LANDING_PAGE.getId());
//    pageRepository.deleteById(CODE_OF_CONDUCT.getId());
//    pageRepository.deleteById(PARTNERS.getId());
//  }
//
//  @Test
//  @SuppressWarnings("unchecked")
//  void testGetTeamPage() {
//    var teamPage = createTeamPageTest(TEAM.getFileName());
//    pageRepository.create(objectMapper.convertValue(teamPage, Map.class));
//    var result = service.getTeam();
//
//    assertEquals(teamPage.section(), result.section());
//    assertEquals(teamPage.contact(), result.contact());
//
//    assertEquals(1, result.membersByType().directors().size());
//    assertEquals(1, result.membersByType().leads().size());
//    assertEquals(1, result.membersByType().evangelists().size());
//
//    assertNull(result.membersByType().directors().getFirst().getMemberTypes());
//    assertNull(result.membersByType().leads().getFirst().getMemberTypes());
//    assertNull(result.membersByType().evangelists().getFirst().getMemberTypes());
//  }
//
//  @Test
//  @SuppressWarnings("unchecked")
//  void testGetFooterPageTest() {
//    var footerPage = createFooterTest(FOOTER.getFileName());
//    pageRepository.create(objectMapper.convertValue(footerPage, Map.class));
//
//    var result = service.getFooter();
//
//    assertEquals(footerPage.title(), result.title());
//    assertEquals(footerPage.subtitle(), result.subtitle());
//    assertEquals(footerPage.description(), result.description());
//
//    assertEquals(6, result.network().size());
//    assertEquals(footerPage.link(), result.link());
//  }
//
//  @Test
//  @SuppressWarnings("unchecked")
//  void testGetCollaboratorPage() {
//    CollaboratorPage collaboratorPage = createCollaboratorPageTest(COLLABORATOR.getFileName());
//    pageRepository.create(objectMapper.convertValue(collaboratorPage, Map.class));
//    var result = service.getCollaborator(DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE);
//
//    assertEquals(collaboratorPage.section(), result.section());
//    assertEquals(collaboratorPage.contact(), result.contact());
//    assertEquals(collaboratorPage.metadata(), result.metadata());
//
//    assertEquals(1, result.collaborators().size());
//
//    assertNotNull(result.collaborators().getFirst().getMemberTypes());
//  }
//
//  @Test
//  @SuppressWarnings("unchecked")
//  void testGetCodeOfConductPage() {
//    CodeOfConductPage codeOfConductPage =
//        createCodeOfConductPageTest(CODE_OF_CONDUCT.getFileName());
//    pageRepository.create(objectMapper.convertValue(codeOfConductPage, Map.class));
//
//    var result = service.getCodeOfConduct();
//
//    assertEquals(codeOfConductPage.section(), result.section());
//    assertEquals(codeOfConductPage.items().size(), result.items().size());
//  }
//
//  @Test
//  @SuppressWarnings("unchecked")
//  void testGetAboutUsPage() {
//    var aboutUsPage = createAboutUsPageTest(ABOUT_US.getFileName());
//    pageRepository.create(objectMapper.convertValue(aboutUsPage, Map.class));
//    var result = service.getAboutUs();
//
//    assertEquals(aboutUsPage.heroSection(), result.heroSection());
//    assertEquals(aboutUsPage.items(), result.items());
//    assertEquals(aboutUsPage.contact(), result.contact());
//  }
//
//  @Test
//  @SuppressWarnings("unchecked")
//  void testGetCelebrateHerPage() {
//    var celebrateHerPage = createCelebrateHerPageTest(CELEBRATE_HER.getFileName());
//    pageRepository.create(objectMapper.convertValue(celebrateHerPage, Map.class));
//    var result = service.getCelebrateHer();
//
//    assertEquals(celebrateHerPage.heroSection(), result.heroSection());
//    assertEquals(celebrateHerPage.section(), result.section());
//    assertEquals(celebrateHerPage.items(), result.items());
//  }
//
//  @SuppressWarnings("unchecked")
//  @Test
//  void testGetLandingPage() {
//    var landingPage = createLandingPageTest(LANDING_PAGE.getFileName());
//    pageRepository.create(objectMapper.convertValue(landingPage, Map.class));
//
//    var result = service.getLandingPage();
//
//    assertEquals(landingPage, result);
//
//    assertEquals(landingPage.getEvents().items().size(), result.getEvents().items().size());
//    assertEquals(landingPage.getProgrammes().items().size(),
// result.getProgrammes().items().size());
//    assertEquals(
//        landingPage.getFeedbackSection().feedbacks().size(),
//        result.getFeedbackSection().feedbacks().size());
//  }
//
//  @Test
//  @SuppressWarnings("unchecked")
//  void testGetPartnersPage() {
//    var partnersPage = createPartnersPageTest(PARTNERS.getFileName());
//    pageRepository.create(objectMapper.convertValue(partnersPage, Map.class));
//
//    var result = service.getPartners();
//
//    assertEquals(partnersPage, result);
//
//    // Assert hero section
//    assertEquals("WCC Partners", result.heroSection().title());
//
//    // Assert intro section
//    assertEquals(4, result.introSection().items().size());
//    assertEquals(
//        "Align with our mission to break barriers and advocate for gender equality in tech.",
//        result.introSection().items().getFirst());
//
//    // Assert contact
//    assertEquals("Become a WCC Partner", result.contact().title());
//    assertEquals("london@womencodingcommunity.com", result.contact().links().getFirst().link());
//    assertEquals("email", result.contact().links().getFirst().type().name().toLowerCase());
//
//    // Assert partner
//    assertEquals(4, result.partners().items().size());
//    assertEquals(
//        "https://drive.google.com/surrealdb-desktop-logo.png",
//        result.partners().items().getFirst().getImages().getFirst().path());
//    assertEquals("SurrealDB", result.partners().items().getFirst().getName());
//    assertEquals(
//        "SurrealDB is an innovative, multi-model, cloud-ready database, suitable"
//            + " for modern and traditional applications.",
//        result.partners().items().getFirst().getDescription());
//    assertEquals("https://surrealdb.com/", result.partners().items().getFirst().getLink().uri());
//  }
// }
