package com.wcc.platform.service;

import static com.wcc.platform.domain.cms.ApiResourcesFile.MENTORSHIP;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipPage;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.utils.FileUtil;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Mentorship service. */
@Service
public class MentorshipService {
  private final ObjectMapper objectMapper;

  @Autowired
  public MentorshipService(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  /**
   * API to retrieve information about mentorship overview.
   *
   * @return Mentorship overview page.
   */
  public MentorshipPage getOverview() {
    try {
      File file = Path.of(FileUtil.getFileUri(MENTORSHIP.getFileName())).toFile();
      return objectMapper.readValue(file, MentorshipPage.class);
    } catch (IOException e) {
      throw new PlatformInternalException(e.getMessage(), e);
    }
  }
}
