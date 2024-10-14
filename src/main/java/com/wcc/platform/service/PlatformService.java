package com.wcc.platform.service;

import com.wcc.platform.domain.cms.pages.FooterPage;
import com.wcc.platform.domain.cms.pages.LandingPage;
import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.platform.Member;
import com.wcc.platform.domain.platform.ResourceContent;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.PageRepository;
import com.wcc.platform.repository.ResourceContentRepository;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/** Platform Service. */
@Service
public class PlatformService {

  private final ResourceContentRepository resource;
  private final PageRepository<FooterPage> footerRepository;
  private final PageRepository<LandingPage> lpRepository;
  private final MemberRepository memberRepository;

  /** Constructor . */
  @Autowired
  public PlatformService(
      @Qualifier("getResourceRepository") final ResourceContentRepository resource,
      final MemberRepository memberRepository,
      @Qualifier("footerRepository") final PageRepository<FooterPage> footerRepository,
      @Qualifier("landingPageRepository") final PageRepository<LandingPage> lpRepository) {
    this.resource = resource;
    this.memberRepository = memberRepository;
    this.footerRepository = footerRepository;
    this.lpRepository = lpRepository;
  }

  public ResourceContent saveResourceContent(final ResourceContent resourceContent) {
    return resource.save(resourceContent);
  }

  /** Save any type of page based on page Type. */
  public Object savePage(final LandingPage page) {
    return lpRepository.save(page);
  }

  /** Save any type of page based on page Type. */
  public Object savePage(final FooterPage page) {
    return footerRepository.save(page);
  }

  public Collection<ResourceContent> getAllResources() {
    return resource.findAll();
  }

  /**
   * Find resource by id or throws {@link ContentNotFoundException} when does not exist.
   *
   * @param id id of resource
   * @return Resource content or not found.
   */
  public ResourceContent getResourceById(final String id) {
    return resource
        .findById(id)
        .orElseThrow(() -> new ContentNotFoundException("Resource not found for id: " + id));
  }

  /**
   * Delete resource if exist otherwise throws {@link ContentNotFoundException}.
   *
   * @param id id of resource
   */
  public void deleteById(final String id) {
    final var result = getResourceById(id);

    resource.deleteById(result.getId());
  }

  /** Save Member into storage. */
  public Member createMember(final Member member) {
    return memberRepository.save(member);
  }

  /**
   * Return all stored members.
   *
   * @return List of members.
   */
  public List<Member> getAll() {
    final var allMembers = memberRepository.getAll();
    if (allMembers == null) {
      return List.of();
    }
    return allMembers;
  }
}
