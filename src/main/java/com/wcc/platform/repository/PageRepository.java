package com.wcc.platform.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.cms.PageType;
import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.utils.FileUtil;
import java.util.Map;

/** Generic page repository interface. */
public interface PageRepository extends CrudRepository<Map<String, Object>, String> {

  /**
   * Default fallback page based on the resources and page type in case the data is not in the
   * database.
   */
  default <T> T getFallback(
      final PageType pageType, final Class<T> valueType, final ObjectMapper objectMapper) {
    try {
      return objectMapper.readValue(FileUtil.readFileAsString(pageType.getFileName()), valueType);
    } catch (JsonProcessingException e) {
      throw new ContentNotFoundException(pageType, e);
    }
  }
}
