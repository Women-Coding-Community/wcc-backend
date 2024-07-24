package com.wcc.platform.service;

import static com.wcc.platform.domain.cms.ApiResourcesFile.CODE_OF_CONDUCT;
import static com.wcc.platform.domain.cms.ApiResourcesFile.COLLABORATOR;
import static com.wcc.platform.domain.cms.ApiResourcesFile.FOOTER;
import static com.wcc.platform.domain.cms.ApiResourcesFile.TEAM;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.cms.pages.CodeOfConductPage;
import com.wcc.platform.domain.cms.pages.CollaboratorPage;
import com.wcc.platform.domain.cms.pages.FooterPage;
import com.wcc.platform.domain.cms.pages.TeamPage;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.utils.FileUtil;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** CMS service responsible for simple pages. */
@Service
public class CmsService {
  private final ObjectMapper objectMapper;

  @Autowired
  public CmsService(final ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  /**
   * Read JSON and convert to Pojo TeamPage.
   *
   * @return Pojo TeamPage.
   */
  public TeamPage getTeam() {
    try {
      return objectMapper.readValue(FileUtil.readFileAsString(TEAM.getFileName()), TeamPage.class);
    } catch (IOException e) {
      throw new PlatformInternalException(e.getMessage(), e);
    }
  }

  /**
   * Read JSON and convert to Pojo FooterPage.
   *
   * @return Footer page
   */
  public FooterPage getFooter() {
    try {
      return objectMapper.readValue(
          FileUtil.readFileAsString(FOOTER.getFileName()), FooterPage.class);
    } catch (IOException e) {
      throw new PlatformInternalException(e.getMessage(), e);
    }
  }

  /**
   * Read JSON and convert to Pojo CollaboratorPage.
   *
   * @return Pojo CollaboratorPage.
   */
  public CollaboratorPage getCollaborator() {
    try {
      final var data = FileUtil.readFileAsString(COLLABORATOR.getFileName());
      return objectMapper.readValue(data, CollaboratorPage.class);
    } catch (IOException e) {
      throw new PlatformInternalException(e.getMessage(), e);
    }
  }

  /**
   * Read JSON and convert to Pojo CodeOfConductPage.
   *
   * @return Pojo CodeOfConductPage.
   */
  public CodeOfConductPage getCodeOfConduct() {
    try {
      final var data = FileUtil.readFileAsString(CODE_OF_CONDUCT.getFileName());
      return objectMapper.readValue(data, CodeOfConductPage.class);
    } catch (IOException e) {
      throw new PlatformInternalException(e.getMessage(), e);
    }
  }
}
