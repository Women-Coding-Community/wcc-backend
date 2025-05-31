package com.wcc.platform.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.cms.PageType;
import com.wcc.platform.domain.cms.pages.FooterSection;
import com.wcc.platform.domain.cms.pages.LandingPage;
import com.wcc.platform.domain.cms.pages.events.EventsPage;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.repository.PageRepository;
import com.wcc.platform.utils.FileUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/** CMS service responsible for simple pages. */
@Service
@AllArgsConstructor
public class CmsService {

  private final ObjectMapper objectMapper;
  private final PageRepository pageRepository;

  /**
   * Find the Footer section in DB and convert to Pojo FooterPage.
   *
   * @return Footer page
   */
  public FooterSection getFooter() {
    final var page = pageRepository.findById(PageType.FOOTER.getId());

    if (page.isPresent()) {
      try {
        return objectMapper.convertValue(page.get(), FooterSection.class);
      } catch (IllegalArgumentException e) {
        throw new PlatformInternalException(e.getMessage(), e);
      }
    }
    return pageRepository.getFallback(PageType.FOOTER, FooterSection.class, objectMapper);
  }

  /**
   * Find Landing page in DB and convert to Pojo FooterPage.
   *
   * @return Landing page of the community.
   */
  public LandingPage getLandingPage() {
    final var page = pageRepository.findById(PageType.LANDING_PAGE.getId());
    if (page.isPresent()) {
      try {
        return objectMapper.convertValue(page.get(), LandingPage.class);
      } catch (IllegalArgumentException e) {
        throw new PlatformInternalException(e.getMessage(), e);
      }
    }
    return pageRepository.getFallback(PageType.LANDING_PAGE, LandingPage.class, objectMapper);
  }

  /**
   * Read Json and convert to POJO event page.
   *
   * @return POJO eventsPage
   */
  public EventsPage getEvents() {
    try {
      final var data = FileUtil.readFileAsString(PageType.EVENTS.getFileName());
      return objectMapper.readValue(data, EventsPage.class);
    } catch (JsonProcessingException e) {
      throw new PlatformInternalException(e.getMessage(), e);
    }
  }
}
