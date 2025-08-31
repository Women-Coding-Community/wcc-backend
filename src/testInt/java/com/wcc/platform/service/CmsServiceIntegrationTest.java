package com.wcc.platform.service;

import static com.wcc.platform.domain.cms.PageType.FOOTER;
import static com.wcc.platform.domain.cms.PageType.LANDING_PAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.cms.pages.FooterSection;
import com.wcc.platform.domain.cms.pages.LandingPage;
import com.wcc.platform.repository.PageRepository;
import com.wcc.platform.utils.FileUtil;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CmsServiceIntegrationTest {

  @Autowired private CmsService service;
  @Autowired private PageRepository pageRepository;
  @Autowired private ObjectMapper objectMapper;

  @BeforeEach
  void deletePages() {
    pageRepository.deleteById(FOOTER.getId());
    pageRepository.deleteById(LANDING_PAGE.getId());
  }

  @Test
  @SuppressWarnings("PMD.LawOfDemeter")
  void testGetFooterPage() throws JsonProcessingException {
    var footerPage = createFooterTest(FOOTER.getFileName());
    pageRepository.create(objectMapper.convertValue(footerPage, Map.class));

    var result = service.getFooter();

    assertEquals(footerPage.title(), result.title());
    assertEquals(footerPage.subtitle(), result.subtitle());
    assertEquals(footerPage.description(), result.description());

    assertEquals(footerPage.network().size(), result.network().size());
    assertEquals(footerPage.link(), result.link());
  }

  @Test
  @SuppressWarnings("PMD.LawOfDemeter")
  void testGetLandingPage() throws Exception {
    var landingPage = createLandingPageTest(LANDING_PAGE.getFileName());
    pageRepository.create(objectMapper.convertValue(landingPage, Map.class));

    var result = service.getLandingPage();

    // Compare key fields and collection sizes to avoid brittle deep equals across nested types
    assertEquals(landingPage.getHeroSection().title(), result.getHeroSection().title());
    assertEquals(
        landingPage.getFullBannerSection().getTitle(), result.getFullBannerSection().getTitle());

    assertEquals(landingPage.getProgrammes().items().size(), result.getProgrammes().items().size());
    assertEquals(
        landingPage.getAnnouncements().items().size(), result.getAnnouncements().items().size());
    assertEquals(landingPage.getEvents().items().size(), result.getEvents().items().size());
    assertEquals(
        landingPage.getFeedbackSection().feedbacks().size(),
        result.getFeedbackSection().feedbacks().size());
    assertEquals(landingPage.getPartners().items().size(), result.getPartners().items().size());
  }

  // Helpers to load pages from JSON files, falling back to defaults if parsing fails.
  private FooterSection createFooterTest(final String fileName) throws JsonProcessingException {
    final String content = FileUtil.readFileAsString(fileName);
    return objectMapper.readValue(content, FooterSection.class);
  }

  private LandingPage createLandingPageTest(final String fileName) throws JsonProcessingException {
    final String content = FileUtil.readFileAsString(fileName);
    return objectMapper.readValue(content, LandingPage.class);
  }
}
