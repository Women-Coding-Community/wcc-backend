package com.wcc.platform.service;

import static com.wcc.platform.domain.cms.ApiResourcesFile.PROG_BOOK_CLUB;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.cms.pages.programme.BookClubPage;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Programme Service. */
@Service
public class ProgrammeService {

  private final ObjectMapper objectMapper;

  @Autowired
  public ProgrammeService(final ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  /**
   * API to fetch the information about BookClub programme.
   *
   * @return BookClub json response
   */
  public BookClubPage getBookClub() {
    try {
      final String data = FileUtil.readFileAsString(PROG_BOOK_CLUB.getFileName());
      return objectMapper.readValue(data, BookClubPage.class);
    } catch (JsonProcessingException e) {
      throw new PlatformInternalException(e.getMessage(), e);
    }
  }
}
