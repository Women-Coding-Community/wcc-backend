package com.wcc.platform.service;

import static com.wcc.platform.factories.SetUpFiltersFactories.createFilterSectionTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.cms.pages.FiltersSection;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class FiltersServiceTest {

  private ObjectMapper objectMapper;

  private FilterService service;

  @BeforeEach
  void setUp() {
    objectMapper = Mockito.mock(ObjectMapper.class);
    service = new FilterService(objectMapper);
  }

  @Test
  void givenInvalidJsonForGetFilterThenThrowException() throws JsonProcessingException {
    when(objectMapper.readValue(anyString(), eq(FiltersSection.class)))
        .thenThrow(new JsonProcessingException("Invalid JSON") {});
    var exception = assertThrows(PlatformInternalException.class, service::getEventsFilters);

    assertEquals("Invalid JSON", exception.getMessage());
  }

  @Test
  void givenValidJsonForGetFilterThenReturnSuccessResponse() throws JsonProcessingException {
    when(objectMapper.readValue(anyString(), eq(FiltersSection.class)))
        .thenReturn(createFilterSectionTest());
    var result = service.getEventsFilters();

    assertEquals(createFilterSectionTest(), result);
  }
}
