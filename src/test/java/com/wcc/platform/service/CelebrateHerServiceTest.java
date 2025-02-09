package com.wcc.platform.service;

import static com.wcc.platform.factories.SetupCelebrateHerFactories.createCelebrateHerPageTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wcc.platform.domain.cms.PageType;
import com.wcc.platform.domain.cms.pages.aboutUs.CelebrateHerPage;
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
class CelebrateHerServiceTest {
  private ObjectMapper objectMapper;
  private PageRepository pageRepository;

  private CelebrateHerService service;

  @BeforeEach
  void setUp() {
    objectMapper = Mockito.mock(ObjectMapper.class);
    objectMapper.registerModule(new JavaTimeModule());
    pageRepository = Mockito.mock(PageRepository.class);
    service = new CelebrateHerService(objectMapper, pageRepository);
  }

  @Test
  @SuppressWarnings("unchecked")
  void whenGetOverviewGivenRecordExistingInDatabaseThenReturnValidResponse() {
    var page = createCelebrateHerPageTest();
    var mapPage =
        new ObjectMapper().registerModule(new JavaTimeModule()).convertValue(page, Map.class);

    when(pageRepository.findById(PageType.CELEBRATE_HER.getId())).thenReturn(Optional.of(mapPage));
    when(objectMapper.convertValue(anyMap(), eq(CelebrateHerPage.class))).thenReturn(page);

    var response = service.getCelebrateHer();

    assertEquals(page, response);
  }

  @Test
  void whenGetOverviewGivenRecordNotInDatabaseThenThrowException() throws IOException {

    when(pageRepository.findById(PageType.CELEBRATE_HER.getId())).thenReturn(Optional.empty());

    var exception = assertThrows(ContentNotFoundException.class, service::getCelebrateHer);

    assertEquals("Content of Page CelebrateHer not found", exception.getMessage());
  }
}
