package com.wcc.platform.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FilterService {

  private final ObjectMapper objectMapper;

  @Autowired
  public FilterService(final ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  /*public FiltersPage getEventsFilters() {
    try {

    } catch (JsonParseException exception) {
      throw new PlatformInternalException(exception.getMessage(), exception);
    }
  }*/
}
