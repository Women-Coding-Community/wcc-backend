package com.wcc.platform.service;

import static com.wcc.platform.factories.SetupMentorshipFactories.createMentorPageTest;
import static com.wcc.platform.factories.SetupMentorshipFactories.createMentorshipConductPageTest;
import static com.wcc.platform.factories.SetupMentorshipFactories.createMentorshipFaqPageTest;
import static com.wcc.platform.factories.SetupMentorshipFactories.createMentorshipPageTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wcc.platform.domain.cms.PageType;
import com.wcc.platform.domain.cms.pages.mentorship.MentorsPage;
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipCodeOfConductPage;
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipFaqPage;
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipPage;
import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.repository.PageRepository;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {
  private ObjectMapper objectMapper;
  private PageRepository pageRepository;

  private MentorshipService service;

  @BeforeEach
  void setUp() {
    objectMapper = Mockito.mock(ObjectMapper.class);
    objectMapper.registerModule(new JavaTimeModule());
    pageRepository = Mockito.mock(PageRepository.class);
    service = new MentorshipService(objectMapper, pageRepository);
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
  @Disabled("Temporary Disable until migrate to postgres")
  void whenGetOverviewGivenRecordNotInDatabaseThenThrowException() {

    when(pageRepository.findById(PageType.MENTORSHIP.getId())).thenReturn(Optional.empty());

    var exception = assertThrows(ContentNotFoundException.class, service::getOverview);

    assertEquals("Content of Page MENTORSHIP not found", exception.getMessage());
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
  @Disabled("Temporary Disable until migrate to postgres")
  void whenGetFaqGivenRecordNotInDatabaseThenThrowException() {
    when(pageRepository.findById(PageType.MENTORSHIP_FAQ.getId())).thenReturn(Optional.empty());
    var exception = assertThrows(ContentNotFoundException.class, service::getFaq);

    assertEquals("Content of Page MENTORSHIP_FAQ not found", exception.getMessage());
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
  @Disabled("Temporary Disable until migrate to postgres")
  void whenGetCodeOfConductGivenRecordNotInDatabaseThenThrowException() {
    when(pageRepository.findById(PageType.MENTORSHIP_CONDUCT.getId())).thenReturn(Optional.empty());
    var exception = assertThrows(ContentNotFoundException.class, service::getCodeOfConduct);

    assertEquals("Content of Page MENTORSHIP_CONDUCT not found", exception.getMessage());
  }

  @Test
  void whenGetMentorsGivenRecordExistingInDatabaseThenReturnValidResponse() {
    var page = createMentorPageTest();
    var mapPage =
        new ObjectMapper().registerModule(new JavaTimeModule()).convertValue(page, Map.class);

    when(pageRepository.findById(PageType.MENTORS.getId())).thenReturn(Optional.of(mapPage));
    when(objectMapper.convertValue(anyMap(), eq(MentorsPage.class))).thenReturn(page);

    var response = service.getMentors();

    assertEquals(page, response);
  }

  @Test
  @Disabled("Temporary Disable until migrate to postgres")
  void whenGetMentorsGivenRecordNotInDatabaseThenThrowException() {
    when(pageRepository.findById(PageType.MENTORS.getId())).thenReturn(Optional.empty());
    var exception = assertThrows(ContentNotFoundException.class, service::getMentors);

    assertEquals("Content of Page MENTORS not found", exception.getMessage());
  }
}
