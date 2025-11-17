package com.wcc.platform.service;

import static com.wcc.platform.domain.cms.PageType.EVENT_FILTERS;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.cms.pages.FiltersSection;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/** Page filters related service. */
@Service
public class FilterService {

  private final ObjectMapper objectMapper;

  @Autowired
  public FilterService(final @Qualifier("objectMapper") ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  /**
   * Parse the eventFiltersSection.json file to FiltersPage.
   *
   * @return {@link FiltersSection}
   */
  public FiltersSection getEventsFilters() {
    try {
      final String filtersData = FileUtil.readFileAsString(EVENT_FILTERS.getFileName());
      return objectMapper.readValue(filtersData, FiltersSection.class);
    } catch (JsonProcessingException exception) {
      throw new PlatformInternalException(exception.getMessage(), exception);
    }
  }
}
