package com.wcc.platform.service;


import static com.wcc.platform.domain.cms.PageType.MENTORSHIP;
import static com.wcc.platform.domain.cms.PageType.MENTORSHIP_FAQ;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.cms.pages.mentorship.FaqPage;
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipPage;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Mentorship service. */
@Service
public class MentorshipService {
  private final ObjectMapper objectMapper;

  @Autowired
  public MentorshipService(final ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  /**
   * API to retrieve information about mentorship overview.
   *
   * @return Mentorship overview page.
   */
  public MentorshipPage getOverview() {
    try {
      final String data = FileUtil.readFileAsString(MENTORSHIP.getFileName());
      return objectMapper.readValue(data, MentorshipPage.class);
    } catch (JsonProcessingException e) {
      throw new PlatformInternalException(e.getMessage(), e);
    }
  }

  public FaqPage getFaq() {
    try {
      final String data = FileUtil.readFileAsString(MENTORSHIP_FAQ.getFileName());
      return objectMapper.readValue(data, FaqPage.class);
    } catch (JsonProcessingException e) {
      throw new PlatformInternalException(e.getMessage(), e);
    }
  }
}
