package com.wcc.platform.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.cms.PageType;
import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.repository.PageRepository;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Page Service. */
@SuppressWarnings("unchecked")
@Slf4j
@Service
public class PageService {

  private final PageRepository pageRepository;

  private final ObjectMapper objectMapper;

  /** Constructor . */
  @Autowired
  public PageService(final PageRepository pageRepository, final ObjectMapper objectMapper) {
    this.pageRepository = pageRepository;
    this.objectMapper = objectMapper;
  }

  /** Update page content based on page Type. */
  public Object update(final PageType pageType, final Object page) {
    pageRepository
        .findById(String.valueOf(pageType.getId()))
        .orElseThrow(
            () -> new ContentNotFoundException("Page not found for id: " + pageType.getId()));

    try {
      final var entity = new HashMap<>(objectMapper.convertValue(page, Map.class));
      entity.put("id", pageType.getId());

      return pageRepository.update(String.valueOf(pageType.getId()), entity);
    } catch (IllegalArgumentException e) {
      log.error("Error while updating page: {}, {}", pageType, page.toString(), e);
      throw new PlatformInternalException(pageType, e);
    }
  }

  /** Create any page. */
  public Object create(final PageType pageType, final Object page) {
    try {
      final var entity = new HashMap<>(objectMapper.convertValue(page, Map.class));
      entity.put("id", pageType.getId());

      return pageRepository.create(entity);
    } catch (IllegalArgumentException e) {
      log.error("Error while creating page: {}, {}", pageType, page.toString(), e);
      throw new PlatformInternalException(pageType, e);
    }
  }

  /** Delete page by id. */
  public void deletePageById(final String id) {
    if (pageRepository.findById(id).isEmpty()) {
      throw new ContentNotFoundException("Page not found for id: " + id);
    }
    pageRepository.deleteById(id);
  }
}
