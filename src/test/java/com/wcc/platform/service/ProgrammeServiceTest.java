package com.wcc.platform.service;

import static com.wcc.platform.factories.SetupProgrammeFactories.createProgrammePageTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.cms.attributes.ProgramType;
import com.wcc.platform.domain.cms.pages.programme.ProgrammePage;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/** Test class for ProgrammeService. */
class ProgrammeServiceTest {
  private ObjectMapper objectMapper;
  private ProgrammeService service;

  @BeforeEach
  void setUp() {
    objectMapper = Mockito.mock(ObjectMapper.class);
    service = new ProgrammeService(objectMapper);
  }

  @Test
  void whenInvalidJsonProvided() throws JsonProcessingException {
    when(objectMapper.readValue(anyString(), eq(ProgrammePage.class)))
        .thenThrow(new JsonProcessingException("Invalid JSON") {});

    var exception =
        assertThrows(
            PlatformInternalException.class, () -> service.getProgramme(ProgramType.BOOK_CLUB));
    assertEquals("Invalid JSON", exception.getMessage());
  }

  @Test
  void whenValidJsonProvided() throws JsonProcessingException {
    var programmePage = createProgrammePageTest("bookClubPage.json");
    when(objectMapper.readValue(anyString(), eq(ProgrammePage.class))).thenReturn(programmePage);

    var response = service.getProgramme(ProgramType.BOOK_CLUB);
    assertEquals(programmePage, response);
  }
}
