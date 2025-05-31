package com.wcc.platform.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wcc.platform.domain.cms.PageType;
import com.wcc.platform.domain.cms.pages.LandingPage;
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
}
