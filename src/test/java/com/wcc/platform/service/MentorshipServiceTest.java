package com.wcc.platform.service;

import static com.wcc.platform.factories.SetupMentorshipFactories.createMentorshipPageTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipPage;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {
  private ObjectMapper objectMapper;

  private MentorshipService service;

  @BeforeEach
  void setUp() {
    objectMapper = Mockito.mock(ObjectMapper.class);
    service = new MentorshipService(objectMapper);
  }

  @Test
  void whenGetOverviewGivenValidJson() throws IOException {
    var page = createMentorshipPageTest("mentorshipPage.json");
    when(objectMapper.readValue(any(String.class), eq(MentorshipPage.class))).thenReturn(page);

    var response = service.getOverview();

    assertEquals(page, response);
  }

  @Test
  void whenGetOverviewGivenInvalidJson() throws IOException {
    when(objectMapper.readValue(anyString(), eq(MentorshipPage.class)))
        .thenThrow(new JsonProcessingException("Invalid JSON") {});

    var exception = assertThrows(PlatformInternalException.class, service::getOverview);
    
    assertEquals("Invalid JSON", exception.getMessage());
  }
}
