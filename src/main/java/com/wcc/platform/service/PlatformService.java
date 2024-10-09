package com.wcc.platform.service;

import com.wcc.platform.domain.platform.Member;
import com.wcc.platform.domain.platform.MemberDto;
import com.wcc.platform.repository.MemberRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Platform service. */
@Service
public class PlatformService {

  private final MemberRepository memberRepository;

  @Autowired
  public PlatformService(final MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
  }

  /** Save Member into storage. */
  public Member createMember(final Member member) {
    // TODO: Check if email exists => save or throw exception
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
   * Update Member data
   *
   * @email member's email as unique identifier
   * @param memberDto MemberDto with updated member's data
   * @return Updated member.
   */
  public Member updateMember(String email, MemberDto memberDto) {
    Optional<Member> memberOptional = emailExists(email);

    Member existingMember =
        memberOptional.orElseThrow(() -> new RuntimeException("Member not found"));
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

  // TODO: Create new service for Member
  /**
   * Update member fields using DTO.
   *
   * @param member member to be updated
   * @param memberDto memberDto with updates
   * @return Updated member
   */
  private Member mergeToMember(Member member, MemberDto memberDto) {
    return member.toBuilder()
        .fullName(memberDto.getFullName())
        .position(memberDto.getPosition())
        .slackDisplayName(memberDto.getSlackDisplayName())
        .country(memberDto.getCountry())
        .city(memberDto.getCity())
        .companyName(memberDto.getCompanyName())
        .network(memberDto.getNetwork())
        .build();
  }
}
