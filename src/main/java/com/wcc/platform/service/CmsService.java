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
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** CMS service responsible for simple pages. */
@Service
public class CmsService {
  private final ObjectMapper objectMapper;

  @Autowired
  public CmsService(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  /**
   * Read JSON and convert to Pojo TeamPage.
   *
   * @return Pojo TeamPage.
   */
  public TeamPage getTeam() {
    try {
      File file = Path.of(FileUtil.getFileUri(TEAM.getFileName())).toFile();
      return objectMapper.readValue(file, TeamPage.class);
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
      File file = Path.of(FileUtil.getFileUri(FOOTER.getFileName())).toFile();
      return objectMapper.readValue(file, FooterPage.class);
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
      File file = Path.of(FileUtil.getFileUri(COLLABORATOR.getFileName())).toFile();
      return objectMapper.readValue(file, CollaboratorPage.class);
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
      File file = Path.of(FileUtil.getFileUri(CODE_OF_CONDUCT.getFileName())).toFile();
      return objectMapper.readValue(file, CodeOfConductPage.class);
    } catch (IOException e) {
      throw new PlatformInternalException(e.getMessage(), e);
    }
  }
}
