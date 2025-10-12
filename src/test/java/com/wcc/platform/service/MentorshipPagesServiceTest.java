package com.wcc.platform.service;

import static com.wcc.platform.factories.SetupMentorshipFactories.createLongTermTimeLinePageTest;
import static com.wcc.platform.factories.SetupMentorshipFactories.createMentorPageTest;
import static com.wcc.platform.factories.SetupMentorshipFactories.createMentorshipAdHocTimelinePageTest;
import static com.wcc.platform.factories.SetupMentorshipFactories.createMentorshipConductPageTest;
import static com.wcc.platform.factories.SetupMentorshipFactories.createMentorshipFaqPageTest;
import static com.wcc.platform.factories.SetupMentorshipFactories.createMentorshipPageTest;
import static com.wcc.platform.factories.SetupMentorshipFactories.createMentorshipStudyGroupPageTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wcc.platform.domain.cms.PageType;
import com.wcc.platform.domain.cms.pages.mentorship.LongTermTimeLinePage;
import com.wcc.platform.domain.cms.pages.mentorship.MentorsPage;
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipAdHocTimelinePage;
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipCodeOfConductPage;
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipFaqPage;
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipPage;
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipStudyGroupsPage;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.repository.PageRepository;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MentorshipPagesServiceTest {
  private ObjectMapper objectMapper;
  private PageRepository pageRepository;
  private MentorshipPagesService service;
  private MentorshipService mentorshipService;

  @BeforeEach
  void setUp() {
    objectMapper = mock(ObjectMapper.class);
    objectMapper.registerModule(new JavaTimeModule());
    pageRepository = mock(PageRepository.class);
    mentorshipService = mock(MentorshipService.class);
    service = new MentorshipPagesService(objectMapper, pageRepository, mentorshipService);
  }

  @Test
  @SuppressWarnings("unchecked")
  void whenGetOverviewGivenRecordExistingInDatabaseThenReturnValidResponse() {
    var page = createMentorshipPageTest();
    var mapPage =
        new ObjectMapper().registerModule(new JavaTimeModule()).convertValue(page, Map.class);

    when(pageRepository.findById(PageType.MENTORSHIP.getId())).thenReturn(Optional.of(mapPage));
    when(objectMapper.convertValue(anyMap(), eq(MentorshipPage.class))).thenReturn(page);

    var response = service.getOverview();

    assertEquals(page, response);
  }

  @Test
  void whenGetOverviewGivenRecordNotInDatabaseThenReturnFallback() {
    var page = createMentorshipPageTest();
    when(pageRepository.getFallback(any(), any(), any())).thenReturn(page);
    when(pageRepository.findById(PageType.MENTORSHIP.getId())).thenReturn(Optional.empty());

    assertEquals(page, service.getOverview());
    verify(pageRepository, times(1)).getFallback(any(), any(), any());
  }

  @Test
  void whenGetFaqGivenRecordExistingInDatabaseThenReturnValidResponse() {
    var page = createMentorshipFaqPageTest();
    var mapPage =
        new ObjectMapper().registerModule(new JavaTimeModule()).convertValue(page, Map.class);

    when(pageRepository.findById(PageType.MENTORSHIP_FAQ.getId())).thenReturn(Optional.of(mapPage));
    when(objectMapper.convertValue(anyMap(), eq(MentorshipFaqPage.class))).thenReturn(page);

    var response = service.getFaq();

    assertEquals(page, response);
  }

  @Test
  void whenGetFaqGivenRecordNotInDatabaseThenReturnFallback() {
    var page = createMentorshipFaqPageTest();
    when(pageRepository.getFallback(any(), any(), any())).thenReturn(page);
    when(pageRepository.findById(PageType.MENTORSHIP_FAQ.getId())).thenReturn(Optional.empty());

    assertEquals(page, service.getFaq());
    verify(pageRepository, times(1)).getFallback(any(), any(), any());
  }

  @Test
  void whenGetCodeOfConductGivenRecordExistingInDatabaseThenReturnValidResponse() {
    var page = createMentorshipConductPageTest();
    var mapPage =
        new ObjectMapper().registerModule(new JavaTimeModule()).convertValue(page, Map.class);

    when(pageRepository.findById(PageType.MENTORSHIP_CONDUCT.getId()))
        .thenReturn(Optional.of(mapPage));
    when(objectMapper.convertValue(anyMap(), eq(MentorshipCodeOfConductPage.class)))
        .thenReturn(page);

    var response = service.getCodeOfConduct();
    assertEquals(page, response);
  }

  @Test
  void whenGetStudyGroupsGivenRecordExistingInDatabaseThenReturnValidResponse() {
    var page = createMentorshipStudyGroupPageTest();
    var mapPage =
        new ObjectMapper().registerModule(new JavaTimeModule()).convertValue(page, Map.class);

    when(pageRepository.findById(PageType.STUDY_GROUPS.getId())).thenReturn(Optional.of(mapPage));
    when(objectMapper.convertValue(anyMap(), eq(MentorshipStudyGroupsPage.class))).thenReturn(page);

    var response = service.getStudyGroups();
    assertEquals(page, response);
  }

  @Test
  void whenGetCodeOfConductGivenRecordNotInDatabaseThenHasFallbackPage() {
    var page = createMentorshipConductPageTest();
    when(pageRepository.getFallback(any(), any(), any())).thenReturn(page);
    when(pageRepository.findById(PageType.MENTORSHIP_CONDUCT.getId())).thenReturn(Optional.empty());

    var response = service.getCodeOfConduct();
    assertEquals(page, response);
    verify(pageRepository, times(1)).getFallback(any(), any(), any());
  }

  @Test
  void whenGetStudyGroupsGivenRecordNotInDatabaseThenHasFallbackPage() {
    var page = createMentorshipStudyGroupPageTest();
    when(pageRepository.getFallback(any(), any(), any())).thenReturn(page);
    when(pageRepository.findById(PageType.STUDY_GROUPS.getId())).thenReturn(Optional.empty());
    var response = service.getStudyGroups();

    assertEquals(page, response);
    verify(pageRepository, times(1)).getFallback(any(), any(), any());
  }

  @Test
  void whenGetMentorsPageGivenRecordExistingInDatabaseThenReturnValidResponse() {
    var page = createMentorPageTest();
    var mapPage =
        new ObjectMapper().registerModule(new JavaTimeModule()).convertValue(page, Map.class);

    when(pageRepository.findById(PageType.MENTORS.getId())).thenReturn(Optional.of(mapPage));
    when(objectMapper.convertValue(anyMap(), eq(MentorsPage.class))).thenReturn(page);
    when(mentorshipService.getMentorsPage(page)).thenReturn(page);

    var response = service.getMentorsPage();

    assertEquals(page, response);
  }

  @Test
  void whenGetMentorsPageGivenRecordNotInDatabaseThenHasFallbackPage() {
    var page = createMentorPageTest();
    when(pageRepository.getFallback(any(), any(), any())).thenReturn(page);
    when(pageRepository.findById(PageType.MENTORS.getId())).thenReturn(Optional.empty());

    var response = service.getMentorsPage();
    assertEquals(page, response);
    verify(pageRepository, times(1)).getFallback(any(), any(), any());
  }

  @Test
  void whenGetAdHocTimelineGivenRecordExistingInDatabaseThenReturnValidResponse() {
    var page = createMentorshipAdHocTimelinePageTest();
    var mapPage =
        new ObjectMapper().registerModule(new JavaTimeModule()).convertValue(page, Map.class);

    when(pageRepository.findById(PageType.AD_HOC_TIMELINE.getId()))
        .thenReturn(Optional.of(mapPage));
    when(objectMapper.convertValue(anyMap(), eq(MentorshipAdHocTimelinePage.class)))
        .thenReturn(page);

    var response = service.getAdHocTimeline();
    assertEquals(page, response);
  }

  @Test
  void whenGetAdHocTimelineGivenRecordNotInDatabaseThenHasFallbackPage() {
    var page = createMentorshipAdHocTimelinePageTest();
    when(pageRepository.getFallback(any(), any(), any())).thenReturn(page);
    when(pageRepository.findById(PageType.AD_HOC_TIMELINE.getId())).thenReturn(Optional.empty());

    assertEquals(page, service.getAdHocTimeline());
    verify(pageRepository, times(1)).getFallback(any(), any(), any());
  }

  @Test
  void whenGetLongTermTimelineGivenRecordExistingInDatabaseThenReturnValidResponse() {
    var page = createLongTermTimeLinePageTest();
    var mapPage =
        new ObjectMapper().registerModule(new JavaTimeModule()).convertValue(page, Map.class);

    when(pageRepository.findById(PageType.MENTORSHIP_LONG_TIMELINE.getId()))
        .thenReturn(Optional.of(mapPage));
    when(objectMapper.convertValue(anyMap(), eq(LongTermTimeLinePage.class))).thenReturn(page);

    var response = service.getLongTermTimeLine();

    assertEquals(page, response);
  }

  @Test
  void whenGetLongTermTimeLineGivenRecordNotInDatabaseThenHasFallbackPage() {
    var page = createLongTermTimeLinePageTest();
    when(pageRepository.getFallback(any(), any(), any())).thenReturn(page);
    when(pageRepository.findById(PageType.MENTORSHIP_LONG_TIMELINE.getId()))
        .thenReturn(Optional.empty());

    var response = service.getLongTermTimeLine();
    assertEquals(page, response);
    verify(pageRepository, times(1)).getFallback(any(), any(), any());
  }

  @Test
  void whenGetLongTermTimeLineGivenIllegalArgumentExceptionThenThrowPlatformInternalException() {
    var page = createLongTermTimeLinePageTest();
    var mapPage =
        new ObjectMapper().registerModule(new JavaTimeModule()).convertValue(page, Map.class);

    when(pageRepository.findById(PageType.MENTORSHIP_LONG_TIMELINE.getId()))
        .thenReturn(Optional.of(mapPage));

    // Mock the objectMapper to throw IllegalArgumentException
    when(objectMapper.convertValue(anyMap(), eq(LongTermTimeLinePage.class)))
        .thenThrow(new IllegalArgumentException("Conversion failed"));

    assertThrows(PlatformInternalException.class, service::getLongTermTimeLine);
  }

  @Test
  void whenGetLongTermTimeLineGivenRepositoryFindByIdThrowsExceptionThenPropagateException() {
    // Test that exceptions from repository.findById are propagated
    when(pageRepository.findById(PageType.MENTORSHIP_LONG_TIMELINE.getId()))
        .thenThrow(new RuntimeException("Database connection failed"));

    // The exception should be propagated
    var exception = assertThrows(RuntimeException.class, service::getLongTermTimeLine);

    assertEquals("Database connection failed", exception.getMessage());

    // Verify that getFallback is never called since the exception occurs before that
    verify(pageRepository, never()).getFallback(any(), any(), any());
  }
}
