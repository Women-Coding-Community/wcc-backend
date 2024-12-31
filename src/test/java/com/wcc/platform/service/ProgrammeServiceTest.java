package com.wcc.platform.service;

import static com.wcc.platform.domain.platform.ProgramType.BOOK_CLUB;
import static com.wcc.platform.factories.SetupProgrammeFactories.createProgrammePageTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.cms.pages.programme.ProgrammePage;
import com.wcc.platform.repository.PageRepository;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

/** Test class for ProgrammeService. */
class ProgrammeServiceTest {
  private ObjectMapper objectMapper;
  private ProgrammeService service;
  @Mock private PageRepository pageRepository;

  @BeforeEach
  void setUp() {
    objectMapper = Mockito.mock(ObjectMapper.class);
    service = new ProgrammeService(pageRepository, objectMapper);
  }

  @Test
  @SuppressWarnings("unchecked")
  void whenGetTeamGivenRecordExistOnDatabaseThenReturnValidResponse() {
    var programmePage = createProgrammePageTest("bookClubPage.json");
    var mapPage = new ObjectMapper().convertValue(programmePage, Map.class);

    when(pageRepository.findById(BOOK_CLUB.toPageId())).thenReturn(Optional.of(mapPage));
    when(objectMapper.convertValue(anyMap(), eq(ProgrammePage.class))).thenReturn(programmePage);

    var response = service.getProgramme(BOOK_CLUB);

    assertEquals(programmePage, response);
  }
}
