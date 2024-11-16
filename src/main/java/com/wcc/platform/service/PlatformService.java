package com.wcc.platform.service;

import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.exceptions.DuplicatedMemberException;
import com.wcc.platform.domain.exceptions.MemberNotFoundException;
import com.wcc.platform.domain.platform.Member;
import com.wcc.platform.domain.platform.MemberDto;
import com.wcc.platform.domain.platform.ResourceContent;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.ResourceContentRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/** Platform Service. */
@Service
public class PlatformService {

  private final ResourceContentRepository resource;
  private final MemberRepository memberRepository;

  @Autowired
  public PlatformService(
      @Qualifier("getResourceRepository") final ResourceContentRepository resource,
      final MemberRepository memberRepository) {
    this.resource = resource;
    this.memberRepository = memberRepository;
  }

  public ResourceContent saveResourceContent(final ResourceContent resourceContent) {
    return resource.save(resourceContent);
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
    Optional<Member> memberOptional = emailExists(member.getEmail());

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
  public Member updateMember(String email, MemberDto memberDto) {
    Optional<Member> memberOptional = emailExists(email);

    Member existingMember = memberOptional.orElseThrow(() -> new MemberNotFoundException(email));
    Member updatedMember = mergeToMember(existingMember, memberDto);
    return memberRepository.update(updatedMember);
  }

  /**
   * Check that member exists.
   *
   * @param email member's email as unique identifier
   * @return Optional with Member object or empty Optional
   */
  private Optional<Member> emailExists(String email) {
    return memberRepository.findByEmail(email);
  }

  /**
   * Update member fields using DTO.
   *
   * @param member member to be updated
   * @param memberDto memberDto with updates
   * @return Updated member
   */
  private Member mergeToMember(Member member, MemberDto memberDto) {
    return member.toBuilder()
        .fullName(memberDto.fullName())
        .position(memberDto.position())
        .slackDisplayName(memberDto.slackDisplayName())
        .country(memberDto.country())
        .city(memberDto.city())
        .companyName(memberDto.companyName())
        .network(memberDto.network())
        .build();
  }
}
