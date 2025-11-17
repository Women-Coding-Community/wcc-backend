package com.wcc.platform.service;

import com.wcc.platform.domain.auth.UserAccount;
import com.wcc.platform.domain.exceptions.DuplicatedMemberException;
import com.wcc.platform.domain.exceptions.MemberNotFoundException;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.member.MemberDto;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.UserAccountRepository;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/** Platform Service. */
@Service
@AllArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final UserAccountRepository userRepository;

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
   * Delete a member by its unique identifier.
   *
   * @param memberId member's unique identifier
   */
  public void deleteMember(final Long memberId) {
    final var mentorId = memberRepository.findById(memberId);
    if (mentorId.isPresent()) {
      memberRepository.deleteById(memberId);
    } else {
      throw new MemberNotFoundException(memberId);
    }
  }

  /**
   * Update Member data.
   *
   * @param memberId member's unique identifier
   * @param memberDto MemberDto with updated member's data
   * @return Updated member.
   */
  public Member updateMember(final Long memberId, final MemberDto memberDto) {
    if (!memberId.equals(memberDto.getId())) {
      throw new IllegalArgumentException("Member ID does not match the provided memberId");
    }

    final Optional<Member> memberOptional = memberRepository.findById(memberId);
    final var member = memberOptional.orElseThrow(() -> new MemberNotFoundException(memberId));

    final Member updatedMember = memberDto.merge(member);
    return memberRepository.update(memberId, updatedMember);
  }

  /**
   * Return all users allowed to access the platform restrict area.
   *
   * @return List of user accounts available.
   */
  public List<UserAccount> getUsers() {
    return userRepository.findAll();
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
}
