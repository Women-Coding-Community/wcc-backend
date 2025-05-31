package com.wcc.platform.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.cms.PageType;
import com.wcc.platform.domain.cms.pages.CollaboratorPage;
import com.wcc.platform.domain.cms.pages.PageMetadata;
import com.wcc.platform.domain.cms.pages.Pagination;
import com.wcc.platform.domain.cms.pages.TeamPage;
import com.wcc.platform.domain.cms.pages.aboutus.AboutUsPage;
import com.wcc.platform.domain.cms.pages.aboutus.CelebrateHerPage;
import com.wcc.platform.domain.cms.pages.aboutus.CodeOfConductPage;
import com.wcc.platform.domain.cms.pages.aboutus.PartnersPage;
import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.domain.platform.Member;
import com.wcc.platform.repository.PageRepository;
import com.wcc.platform.utils.PaginationUtil;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/** CMS service responsible for simple pages. */
@Service
@AllArgsConstructor
public class CmsAboutUsService {

  private final ObjectMapper objectMapper;
  private final PageRepository pageRepository;

  /**
   * Find the Team page in DB and convert to Pojo TeamPage.
   *
   * @return Pojo TeamPage.
   */
  public TeamPage getTeam() {
    final var page = pageRepository.findById(PageType.TEAM.getId());

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
   * Find the Collaborators page in DB and convert to Pojo CollaboratorPage.
   *
   * @return Collaborators page content.
   */
  public CollaboratorPage getCollaborator(final int currentPage, final int pageSize) {
    final var pageOptional = pageRepository.findById(PageType.COLLABORATOR.getId());

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
            PageType.COLLABORATOR.getId(),
            new PageMetadata(paginationRecord),
            page.heroSection(),
            page.section(),
            page.contact(),
            pagCollaborators);

      } catch (IllegalArgumentException e) {
        throw new PlatformInternalException(e.getMessage(), e);
      }
    }

    throw new ContentNotFoundException(PageType.COLLABORATOR);
  }

  /**
   * Find the Code of conduct page in DB and convert to Pojo CodeOfConductPage.
   *
   * @return Pojo CodeOfConductPage.
   */
  public CodeOfConductPage getCodeOfConduct() {
    final var pageOptional = pageRepository.findById(PageType.CODE_OF_CONDUCT.getId());

    if (pageOptional.isPresent()) {
      try {
        return objectMapper.convertValue(pageOptional.get(), CodeOfConductPage.class);
      } catch (IllegalArgumentException e) {
        throw new PlatformInternalException(e.getMessage(), e);
      }
    }

    throw new ContentNotFoundException(PageType.CODE_OF_CONDUCT);
  }

  /**
   * Find About Us page in DB and convert to Pojo AboutUs.
   *
   * @return Pojo AboutUs
   */
  public AboutUsPage getAboutUs() {
    final var page = pageRepository.findById(PageType.ABOUT_US.getId());

    if (page.isPresent()) {
      try {
        return objectMapper.convertValue(page.get(), AboutUsPage.class);
      } catch (IllegalArgumentException e) {
        throw new PlatformInternalException(e.getMessage(), e);
      }
    }

    throw new ContentNotFoundException(PageType.ABOUT_US);
  }

  /**
   * Find Celebrate Her page in DB and convert to Pojo CelebrateHer page.
   *
   * @return Pojo CelebrateHer page
   */
  public CelebrateHerPage getCelebrateHer() {
    final var page = pageRepository.findById(PageType.CELEBRATE_HER.getId());

    if (page.isPresent()) {
      try {
        return objectMapper.convertValue(page.get(), CelebrateHerPage.class);
      } catch (IllegalArgumentException e) {
        throw new PlatformInternalException(e.getMessage(), e);
      }
    }

    throw new ContentNotFoundException(PageType.CELEBRATE_HER);
  }

  /**
   * Find Partners page in DB and convert to Pojo PartnersPage.
   *
   * @return Pojo PartnersPage.
   */
  public PartnersPage getPartners() {
    final var page = pageRepository.findById(PageType.PARTNERS.getId());

    if (page.isPresent()) {
      try {
        return objectMapper.convertValue(page.get(), PartnersPage.class);
      } catch (IllegalArgumentException e) {
        throw new PlatformInternalException(e.getMessage(), e);
      }
    }

    throw new ContentNotFoundException(PageType.PARTNERS);
  }
}
