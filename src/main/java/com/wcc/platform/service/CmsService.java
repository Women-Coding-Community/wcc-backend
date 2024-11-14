package com.wcc.platform.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.cms.PageType;
import com.wcc.platform.domain.cms.pages.CodeOfConductPage;
import com.wcc.platform.domain.cms.pages.CollaboratorPage;
import com.wcc.platform.domain.cms.pages.EventsPage;
import com.wcc.platform.domain.cms.pages.FooterPage;
import com.wcc.platform.domain.cms.pages.LandingPage;
import com.wcc.platform.domain.cms.pages.TeamPage;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.repository.PageRepository;
import com.wcc.platform.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/** CMS service responsible for simple pages. */
@Service
public class CmsService {
  private final ObjectMapper objectMapper;
  private final PageRepository<FooterPage> footerRepository;
  private final PageRepository<LandingPage> lpRepository;

  /** Init repositories with respective qualifiers. */
  @Autowired
  public CmsService(
      final ObjectMapper objectMapper,
      @Qualifier("footerRepository") final PageRepository<FooterPage> footerRepository,
      @Qualifier("landingPageRepository") final PageRepository<LandingPage> lpRepository) {
    this.objectMapper = objectMapper;
    this.footerRepository = footerRepository;
    this.lpRepository = lpRepository;
  }

  /**
   * Read JSON and convert to Pojo TeamPage.
   *
   * @return Pojo TeamPage.
   */
  public TeamPage getTeam() {
    try {
      return objectMapper.readValue(
          FileUtil.readFileAsString(PageType.TEAM.getFileName()), TeamPage.class);
    } catch (JsonProcessingException e) {
      throw new PlatformInternalException(e.getMessage(), e);
    }
  }

  /**
   * Read JSON and convert to Pojo FooterPage.
   *
   * @return Footer page
   */
  public FooterPage getFooter() {
    final var page = footerRepository.findById(PageType.FOOTER.name());
    return page.orElseGet(this::getFooterFallback);
  }

  private FooterPage getFooterFallback() {
    try {
      return objectMapper.readValue(
          FileUtil.readFileAsString(PageType.FOOTER.getFileName()), FooterPage.class);
    } catch (JsonProcessingException e) {
      throw new PlatformInternalException(e.getMessage(), e);
    }
  }

  /**
   * Read JSON and convert to Pojo FooterPage.
   *
   * @return Landing page of the community.
   */
  public LandingPage getLandingPage() {
    final var page = lpRepository.findById(PageType.LANDING_PAGE.name());

    return page.orElseGet(this::getLandingPageFallback);
  }

  private LandingPage getLandingPageFallback() {
    try {
      return objectMapper.readValue(
          FileUtil.readFileAsString(PageType.LANDING_PAGE.getFileName()), LandingPage.class);
    } catch (JsonProcessingException e) {
      throw new PlatformInternalException(e.getMessage(), e);
    }
  }

  /**
   * Read JSON and convert to Pojo CollaboratorPage.
   *
   * @return Collaborators page content.
   */
  public CollaboratorPage getCollaborator() {
    try {
      return objectMapper.readValue(
          FileUtil.readFileAsString(PageType.COLLABORATOR.getFileName()), CollaboratorPage.class);
    } catch (JsonProcessingException e) {
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
      final var data = FileUtil.readFileAsString(PageType.CODE_OF_CONDUCT.getFileName());
      return objectMapper.readValue(data, CodeOfConductPage.class);
    } catch (JsonProcessingException e) {
      throw new PlatformInternalException(e.getMessage(), e);
    }
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
