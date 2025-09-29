package com.wcc.platform.service;

import com.wcc.platform.domain.exceptions.DuplicatedMemberException;
import com.wcc.platform.domain.exceptions.MemberNotFoundException;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.member.MemberDto;
import com.wcc.platform.repository.MemberRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Platform Service. */
@Service
public class PlatformService {

  private final MemberRepository memberRepository;

  /** Constructor . */
  @Autowired
  public PlatformService(final MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
  }

  /** Save Member into storage. */
  public Member createMember(final Member member) {
    final Optional<Member> memberOptional = emailExists(member.getEmail());

    if (memberOptional.isPresent()) {
      throw new DuplicatedMemberException(member.getEmail());
    }

    return memberRepository.create(member);
  }

  /**
   * Return all stored members.
   *
   * @return List of members.
   */
  public List<Member> getAllMembers() {
    final var allMembers = memberRepository.getAll();
    if (allMembers == null) {
      return List.of();
    }
    return allMembers;
  }

  /**
   * Update Member data.
   *
   * @param memberId member's unique identifier
   * @param memberDto MemberDto with updated member's data
   * @return Updated member.
   */
  public Member updateMember(final Long memberId, final MemberDto memberDto) {
    final Optional<Member> memberOptional = memberRepository.findById(memberId);
    final var member = memberOptional.orElseThrow(() -> new MemberNotFoundException(memberId));

    final Member updatedMember = mergeToMember(member, memberDto);
    return memberRepository.update(memberId, updatedMember);
  }

  /**
   * Check that a member exists.
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
        .id(member.getId())
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
