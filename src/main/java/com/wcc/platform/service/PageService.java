package com.wcc.platform.service;

import com.wcc.platform.domain.cms.pages.FooterPage;
import com.wcc.platform.domain.cms.pages.LandingPage;
import com.wcc.platform.repository.PageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/** Pages Service. */
@Service
public class PageService {

  private final PageRepository<FooterPage> footerRepository;
  private final PageRepository<LandingPage> lpRepository;

  /** Constructor . */
  @Autowired
  public PageService(
      @Qualifier("footerRepository") final PageRepository<FooterPage> footerRepository,
      @Qualifier("landingPageRepository") final PageRepository<LandingPage> lpRepository) {
    this.footerRepository = footerRepository;
    this.lpRepository = lpRepository;
  }

  /** Save any type of page based on page Type. */
  public Object update(final LandingPage page) {
    return lpRepository.update(page.getId(), page);
  }

  /** Save any type of page based on page Type. */
  public Object update(final FooterPage page) {
    return footerRepository.update(page.id(), page);
  }

  /** Save any type of page based on page Type. */
  public Object create(final FooterPage page) {
    return footerRepository.create(page);
  }

  /** Save any type of page based on page Type. */
  public Object create(final LandingPage page) {
    return lpRepository.create(page);
  }

  /** return all pages. */
  public Object findAll() {
    return footerRepository.findAll();
  }
}
