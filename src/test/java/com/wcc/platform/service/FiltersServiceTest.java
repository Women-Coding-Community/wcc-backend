package com.wcc.platform.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class FiltersServiceTest {

  private ObjectMapper objectMapper;

  /*  private FilterService service;

  @BeforeEach
  void setUp() {
    objectMapper = Mockito.mock(ObjectMapper.class);
    service = new FilterService(objectMapper);
  }*/

  @Test
  void givenInvalidJsonForGetFilterThenThrowException() {}

  @Test
  void givenValidJsonForGetFilterThenReturnSuccessResponse() {}
}
