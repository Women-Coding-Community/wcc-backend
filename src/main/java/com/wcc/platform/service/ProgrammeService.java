package com.wcc.platform.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.cms.PageType;
import com.wcc.platform.domain.cms.pages.programme.ProgrammePage;
import com.wcc.platform.domain.exceptions.InvalidProgramTypeException;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.domain.platform.ProgramType;
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
   * API to fetch details about different type of programmes.
   *
   * @return Programme Page json response
   */
  public ProgrammePage getProgramme(final ProgramType programType) {
    if (ProgramType.BOOK_CLUB.equals(programType)) {
      try {
        final String data = FileUtil.readFileAsString(PageType.PROG_BOOK_CLUB.getFileName());

        return objectMapper.readValue(data, ProgrammePage.class);
      } catch (JsonProcessingException e) {
        throw new PlatformInternalException(e.getMessage(), e);
      }
    }

    throw new InvalidProgramTypeException(programType.toString() + " is Invalid");
  }
}
