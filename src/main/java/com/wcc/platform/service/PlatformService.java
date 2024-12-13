package com.wcc.platform.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.cms.PageType;
import com.wcc.platform.domain.cms.pages.FooterPage;
import com.wcc.platform.domain.cms.pages.LandingPage;
import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.exceptions.DuplicatedMemberException;
import com.wcc.platform.domain.exceptions.MemberNotFoundException;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.domain.platform.Member;
import com.wcc.platform.domain.platform.MemberDto;
import com.wcc.platform.domain.platform.ResourceContent;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.PageRepository;
import com.wcc.platform.repository.ResourceContentRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
  @SuppressWarnings("unchecked")
  public Object savePage(final LandingPage page) {
    try {
      return pageRepository.save(objectMapper.convertValue(page, Map.class));
    } catch (IllegalArgumentException e) {
      throw new PlatformInternalException(PageType.LANDING_PAGE, e);
    }
  }

  /** Save any type of page based on page Type. */
  @SuppressWarnings("unchecked")
  public Object savePage(final FooterPage page) {
    try {
      return pageRepository.save(objectMapper.convertValue(page, Map.class));
    } catch (IllegalArgumentException e) {
      throw new PlatformInternalException(PageType.FOOTER, e);
    }
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

  /** Delete page by id. */
  public void deletePageById(final String id) {
    if (pageRepository.findById(id).isEmpty()) {
      throw new ContentNotFoundException("Page not found for id: " + id);
    }
    pageRepository.deleteById(id);
  }

  /** Save Member into storage. */
  public Member createMember(final Member member) {
    final Optional<Member> memberOptional = emailExists(member.getEmail());

    if (memberOptional.isPresent()) {
      throw new DuplicatedMemberException(member.getEmail());
    }

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

  /**
   * Update Member data.
   *
   * @param email member's email as unique identifier
   * @param memberDto MemberDto with updated member's data
   * @return Updated member.
   */
  public Member updateMember(final String email, final MemberDto memberDto) {
    final Optional<Member> memberOptional = emailExists(email);

    final Member existingMember =
        memberOptional.orElseThrow(() -> new MemberNotFoundException(email));
    final Member updatedMember = mergeToMember(existingMember, memberDto);
    return memberRepository.update(updatedMember);
  }

  /**
   * Check that member exists.
   *
   * @param email member's email as unique identifier
   * @return Optional with Member object or empty Optional
   */
  private Optional<Member> emailExists(final String email) {
    return memberRepository.findByEmail(email);
  }

  /**
   * Update member fields using DTO.
   *
   * @param member member to be updated
   * @param memberDto memberDto with updates
   * @return Updated member
   */
  private Member mergeToMember(final Member member, final MemberDto memberDto) {
    return member.toBuilder()
        .fullName(memberDto.fullName())
        .position(memberDto.position())
        .slackDisplayName(memberDto.slackDisplayName())
        .country(memberDto.country())
        .city(memberDto.city())
        .companyName(memberDto.companyName())
        .images(memberDto.images())
        .network(memberDto.network())
        .build();
  }
}
