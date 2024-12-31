package com.wcc.platform.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.cms.PageType;
import com.wcc.platform.domain.cms.pages.FooterSection;
import com.wcc.platform.domain.cms.pages.LandingPage;
import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.repository.PageRepository;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Pages Service. */
@SuppressWarnings("unchecked")
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

  /** Save any type of page based on page Type. */
  public Object update(final LandingPage page) {
    try {
      return pageRepository.update(page.getId(), objectMapper.convertValue(page, Map.class));
    } catch (IllegalArgumentException e) {
      throw new PlatformInternalException(PageType.LANDING_PAGE, e);
    }
  }

  /** Save any type of page based on page Type. */
  public Object update(final FooterSection page) {
    try {
      return pageRepository.update(page.id(), objectMapper.convertValue(page, Map.class));
    } catch (IllegalArgumentException e) {
      throw new PlatformInternalException(PageType.FOOTER, e);
    }
  }

  /** Create footer page. */
  public Object create(final FooterSection page) {
    try {
      return pageRepository.create(objectMapper.convertValue(page, Map.class));
    } catch (IllegalArgumentException e) {
      throw new PlatformInternalException(PageType.FOOTER, e);
    }
  }

  /** Create landing page. */
  public Object create(final LandingPage page) {
    try {
      return pageRepository.create(objectMapper.convertValue(page, Map.class));
    } catch (IllegalArgumentException e) {
      throw new PlatformInternalException(PageType.LANDING_PAGE, e);
    }
  }

  /** Create any page. */
  public Object create(final Object page) {
    try {
      return pageRepository.create(objectMapper.convertValue(page, Map.class));
    } catch (IllegalArgumentException e) {
      throw new PlatformInternalException(page.toString(), e);
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
