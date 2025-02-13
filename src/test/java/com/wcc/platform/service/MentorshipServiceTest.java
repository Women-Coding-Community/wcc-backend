package com.wcc.platform.service;

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
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipFaqPage;
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipPage;
import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.repository.PageRepository;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
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
  void whenGetOverviewGivenRecordNotInDatabaseThenThrowException() throws IOException {

    when(pageRepository.findById(PageType.MENTORSHIP.getId())).thenReturn(Optional.empty());

    var exception = assertThrows(ContentNotFoundException.class, service::getOverview);

    assertEquals("Content of Page MENTORSHIP not found", exception.getMessage());
  }

  @Test
  void whenGetFaqGivenRecordExistingInDatabaseThenReturnValidResponse() throws IOException {
    var page = createMentorshipFaqPageTest();
    var mapPage =
        new ObjectMapper().registerModule(new JavaTimeModule()).convertValue(page, Map.class);

    when(pageRepository.findById(PageType.MENTORSHIP_FAQ.getId())).thenReturn(Optional.of(mapPage));
    when(objectMapper.convertValue(anyMap(), eq(MentorshipFaqPage.class))).thenReturn(page);

    var response = service.getFaq();

    assertEquals(page, response);
  }

  @Test
  void whenGetFaqGivenRecordNotInDatabaseThenThrowException() throws IOException {
    when(pageRepository.findById(PageType.MENTORSHIP_FAQ.getId())).thenReturn(Optional.empty());

    var exception = assertThrows(ContentNotFoundException.class, service::getFaq);

    assertEquals("Content of Page MENTORSHIP_FAQ not found", exception.getMessage());
  }
}
