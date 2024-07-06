package com.wcc.platform.service;

import static com.wcc.platform.factories.TestMentorshipFactories.createMentorshipPageTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipPage;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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
    when(objectMapper.readValue(any(File.class), eq(MentorshipPage.class))).thenReturn(page);

    var response = service.getOverview();

    assertEquals(page, response);
  }

  @Test
  void whenGetOverviewGivenInvalidJson() throws IOException {
    when(objectMapper.readValue(any(File.class), eq(MentorshipPage.class)))
        .thenThrow(new IOException("Invalid JSON"));
    var exception = assertThrows(PlatformInternalException.class, () -> service.getOverview());

    assertEquals("Invalid JSON", exception.getMessage());
  }
}
