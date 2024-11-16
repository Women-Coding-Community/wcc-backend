package com.wcc.platform.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.cms.PageType;
import com.wcc.platform.domain.cms.pages.FooterPage;
import com.wcc.platform.domain.cms.pages.LandingPage;
import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
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
  private final PageRepository pageRepository;
  private final MemberRepository memberRepository;
  private final ObjectMapper objectMapper;

  /** Constructor . */
  @Autowired
  public PlatformService(
      @Qualifier("getResourceRepository") final ResourceContentRepository resource,
      final MemberRepository memberRepository,
      final PageRepository pageRepository,
      final ObjectMapper objectMapper) {
    this.resource = resource;
    this.memberRepository = memberRepository;
    this.pageRepository = pageRepository;
    this.objectMapper = objectMapper;
  }

  public ResourceContent saveResourceContent(final ResourceContent resourceContent) {
    return resource.save(resourceContent);
  }

  /** Save any type of page based on page Type. */
  public Object savePage(final LandingPage page) {
    try {
      return pageRepository.save(objectMapper.writeValueAsString(page));
    } catch (JsonProcessingException e) {
      throw new PlatformInternalException(PageType.LANDING_PAGE, e);
    }
  }

  /** Save any type of page based on page Type. */
  public Object savePage(final FooterPage page) {
    try {
      return pageRepository.save(objectMapper.writeValueAsString(page));
    } catch (JsonProcessingException e) {
      throw new PlatformInternalException(PageType.FOOTER, e);
    }
  }

  public Collection<String> getAllPages() {
    return pageRepository.findAll();
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

  public void deletePageById(final String id) {
    pageRepository.findById(id).ifPresent(pageRepository::deleteById);
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
