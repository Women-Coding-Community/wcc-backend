package com.wcc.platform.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.cms.PageType;
import com.wcc.platform.domain.cms.pages.AboutUsPage;
import com.wcc.platform.domain.cms.pages.CodeOfConductPage;
import com.wcc.platform.domain.cms.pages.CollaboratorPage;
import com.wcc.platform.domain.cms.pages.FooterPage;
import com.wcc.platform.domain.cms.pages.LandingPage;
import com.wcc.platform.domain.cms.pages.PageMetadata;
import com.wcc.platform.domain.cms.pages.Pagination;
import com.wcc.platform.domain.cms.pages.TeamPage;
import com.wcc.platform.domain.cms.pages.events.EventsPage;
import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.domain.platform.Member;
import com.wcc.platform.repository.PageRepository;
import com.wcc.platform.utils.FileUtil;
import com.wcc.platform.utils.PaginationUtil;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** CMS service responsible for simple pages. */
@Service
public class CmsService {

  private final ObjectMapper objectMapper;
  private final PageRepository pageRepository;

  /** Init repositories with respective qualifiers. */
  @Autowired
  public CmsService(final ObjectMapper objectMapper, final PageRepository pageRepository) {
    this.objectMapper = objectMapper;
    this.pageRepository = pageRepository;
  }

  /**
   * Find Team page in DB and convert to Pojo TeamPage.
   *
   * @return Pojo TeamPage.
   */
  public TeamPage getTeam() {
    final var page = pageRepository.findById(PageType.TEAM.getPageId());

    if (page.isPresent()) {
      try {
        return objectMapper.convertValue(page.get(), TeamPage.class);
      } catch (IllegalArgumentException e) {
        throw new PlatformInternalException(e.getMessage(), e);
      }
    }

    throw new ContentNotFoundException(PageType.TEAM);
  }

  /**
   * Find Footer section in DB and convert to Pojo FooterPage.
   *
   * @return Footer page
   */
  public FooterPage getFooter() {
    final var page = pageRepository.findById(PageType.FOOTER.getPageId());

    if (page.isPresent()) {
      try {
        return objectMapper.convertValue(page.get(), FooterPage.class);
      } catch (IllegalArgumentException e) {
        throw new PlatformInternalException(e.getMessage(), e);
      }
    }

    throw new ContentNotFoundException(PageType.FOOTER);
  }

  /**
   * Find Landing page in DB and convert to Pojo FooterPage.
   *
   * @return Landing page of the community.
   */
  public LandingPage getLandingPage() {
    final var page = pageRepository.findById(PageType.LANDING_PAGE.getPageId());

    if (page.isPresent()) {
      try {
        return objectMapper.convertValue(page.get(), LandingPage.class);
      } catch (IllegalArgumentException e) {
        throw new PlatformInternalException(e.getMessage(), e);
      }
    }

    throw new ContentNotFoundException(PageType.LANDING_PAGE);
  }

  /**
   * Find Collaborators page in DB and convert to Pojo CollaboratorPage.
   *
   * @return Collaborators page content.
   */
  public CollaboratorPage getCollaborator(final int currentPage, final int pageSize) {
    final var pageOptional = pageRepository.findById(PageType.COLLABORATOR.getPageId());

    if (pageOptional.isPresent()) {
      try {
        final var page = objectMapper.convertValue(pageOptional.get(), CollaboratorPage.class);
        final var allCollaborators = page.collaborators();
        final List<Member> pagCollaborators =
            PaginationUtil.getPaginatedResult(allCollaborators, currentPage, pageSize);

        final Pagination paginationRecord =
            new Pagination(
                allCollaborators.size(),
                PaginationUtil.getTotalPages(allCollaborators, pageSize),
                currentPage,
                pageSize);

        return new CollaboratorPage(
            PageType.COLLABORATOR.getPageId(),
            new PageMetadata(paginationRecord),
            page.page(),
            page.contact(),
            pagCollaborators);

      } catch (IllegalArgumentException e) {
        throw new PlatformInternalException(e.getMessage(), e);
      }
    }

    throw new ContentNotFoundException(PageType.COLLABORATOR);
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

  /**
   * Find About Us page in DB and convert to Pojo AboutUs.
   *
   * @return Pojo AboutUs
   */
  public AboutUsPage getAboutUs() {
    final var page = pageRepository.findById(PageType.ABOUT_US.getPageId());

    if (page.isPresent()) {
      try {
        return objectMapper.convertValue(page.get(), AboutUsPage.class);
      } catch (IllegalArgumentException e) {
        throw new PlatformInternalException(e.getMessage(), e);
      }
    }

    throw new ContentNotFoundException(PageType.ABOUT_US);
  }
}
