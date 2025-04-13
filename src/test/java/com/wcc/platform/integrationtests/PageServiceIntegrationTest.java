// package com.wcc.platform.integrationtests;
//
// import static com.wcc.platform.domain.cms.PageType.ABOUT_US;
// import static com.wcc.platform.domain.cms.PageType.COLLABORATOR;
// import static org.junit.jupiter.api.Assertions.assertEquals;
//
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.wcc.platform.factories.SetupFactories;
// import com.wcc.platform.repository.PageRepository;
// import com.wcc.platform.service.PageService;
// import java.util.Map;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
// import org.springframework.test.context.ActiveProfiles;
//
// @ActiveProfiles("test")
// @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
// class PageServiceIntegrationTest extends SurrealDbIntegrationTest {
//
//  @Autowired private PageService service;
//  @Autowired private PageRepository pageRepository;
//  @Autowired private ObjectMapper objectMapper;
//
//  @BeforeEach
//  void deletePages() {
//    pageRepository.deleteById(COLLABORATOR.getId());
//    pageRepository.deleteById(ABOUT_US.getId());
//  }
//
//  @Test
//  @DisplayName(
//      "Given pageType id When create page And id is not the same as pageType "
//          + "Then override content id with pageType")
//  void testCreatePageIncorrectId() {
//    var page = SetupFactories.createTeamPageTest();
//
//    var result = service.create(COLLABORATOR, page);
//
//    assertEquals(COLLABORATOR.getId(), ((Map) result).get("id"));
//  }
//
//  @Test
//  @DisplayName(
//      "Given pageType id When update page And id is not the same as pageType "
//          + "Then override content id with pageType")
//  void testUpdatePageIncorrectId() {
//    var teamPage = SetupFactories.createTeamPageTest();
//    var aboutPage = SetupFactories.createAboutUsPageTest();
//    pageRepository.create(objectMapper.convertValue(aboutPage, Map.class));
//
//    var result = service.update(ABOUT_US, teamPage);
//
//    assertEquals(ABOUT_US.getId(), ((Map) result).get("id"));
//  }
// }
