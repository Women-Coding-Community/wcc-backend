package com.wcc.platform.service;

import static com.wcc.platform.domain.cms.PageType.MENTORSHIP;
import static com.wcc.platform.domain.cms.PageType.MENTORSHIP_FAQ;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipFaqPage;
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipPage;
import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.repository.PageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Mentorship service. */
@Service
public class MentorshipService {
  private final ObjectMapper objectMapper;
  private final PageRepository pageRepository;

  @Autowired
  public MentorshipService(final ObjectMapper objectMapper, final PageRepository pageRepository) {
    this.objectMapper = objectMapper;
    this.pageRepository = pageRepository;
  }

  /**
   * API to retrieve information about mentorship overview.
   *
   * @return Mentorship overview page.
   */
  public MentorshipPage getOverview() {
    final var page = pageRepository.findById(MENTORSHIP.getId());
    if (page.isPresent()) {
      try {
        return objectMapper.convertValue(page.get(), MentorshipPage.class);
      } catch (IllegalArgumentException e) {
        throw new PlatformInternalException(e.getMessage(), e);
      }
    }
    throw new ContentNotFoundException(MENTORSHIP);
  }

  /**
   * API to retrieve information about mentorship faq.
   *
   * @return Mentorship faq page.
   */
  public MentorshipFaqPage getFaq() {
    final var page = pageRepository.findById(MENTORSHIP_FAQ.getId());
    if (page.isPresent()) {
      try {
        return objectMapper.convertValue(page.get(), MentorshipFaqPage.class);
      } catch (IllegalArgumentException e) {
        throw new PlatformInternalException(e.getMessage(), e);
      }
    }
    throw new ContentNotFoundException(MENTORSHIP_FAQ);
  }
}
