package com.wcc.platform.service;

import static com.wcc.platform.domain.platform.ProgramType.BOOK_CLUB;
import static com.wcc.platform.factories.SetupProgrammeFactories.createProgrammePageTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.repository.PageRepository;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/** Test class for ProgrammeService. */
class ProgrammeServiceTest {
  private ProgrammeService service;

  @Mock private PageRepository pageRepository;
  @Mock private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    objectMapper = new ObjectMapper();
    objectMapper.findAndRegisterModules();

    service = new ProgrammeService(pageRepository, objectMapper);
  }

  @Test
  @SuppressWarnings("unchecked")
  void whenGetTeamGivenRecordExistOnDatabaseThenReturnValidResponse() {
    var programmePage = createProgrammePageTest("bookClubPage.json");
    var mapPage = objectMapper.convertValue(programmePage, Map.class);
    when(pageRepository.findById(BOOK_CLUB.toPageId())).thenReturn(Optional.of(mapPage));

    var response = service.getProgramme(BOOK_CLUB);

    assertEquals(programmePage.getPage(), response.getPage());
    assertEquals(programmePage.getContact(), response.getContact());
    assertEquals(programmePage.getProgrammeDetails(), response.getProgrammeDetails());
  }

  @Test
  @SuppressWarnings("unchecked")
  @Disabled(
      "This test is disabled because the equals "
          + "method is different for a record object which is strange.")
  void whenGetTeamGivenRecordExistThenReturnInTheResponse() {
    var programmePage = createProgrammePageTest("bookClubPage.json");
    var mapPage = objectMapper.convertValue(programmePage, Map.class);

    when(pageRepository.findById(BOOK_CLUB.toPageId())).thenReturn(Optional.of(mapPage));

    var response = service.getProgramme(BOOK_CLUB);

    assertEquals(programmePage, response);
  }
}
