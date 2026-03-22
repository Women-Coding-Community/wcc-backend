package com.wcc.platform.service;

import com.wcc.platform.domain.auth.UserAccount;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.attributes.ImageType;
import com.wcc.platform.domain.exceptions.DuplicatedMemberException;
import com.wcc.platform.domain.exceptions.MemberNotFoundException;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.member.MemberDto;
import com.wcc.platform.domain.platform.type.RoleType;
import com.wcc.platform.domain.resource.MemberProfilePicture;
import com.wcc.platform.domain.resource.Resource;
import com.wcc.platform.repository.MemberProfilePictureRepository;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.UserAccountRepository;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** Platform Service. */
@Slf4j
@Service
@AllArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final UserAccountRepository userRepository;
  private final MemberProfilePictureRepository profilePicRepo;
  private final UserProvisionService userProvisionService;

  /** Save Member into storage. */
  public Member createMember(final Member member) {
    final Optional<Member> memberOptional = emailExists(member.getEmail());
    final var userExists = userRepository.findByEmail(member.getEmail()).isPresent();
    if (memberOptional.isPresent()) {
      throw new DuplicatedMemberException(member.getEmail());
    }
    final var createdMember = memberRepository.create(member);
    if (!userExists) {
      userProvisionService.provisionUserRole(
          createdMember.getId(), createdMember.getEmail(), RoleType.VIEWER);
    }
    return createdMember;
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
    return allMembers.stream().map(this::enrichWithProfilePicture).toList();
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

  private Member enrichWithProfilePicture(final Member member) {
    final Optional<Image> profilePicture = fetchProfilePicture(member.getId());

    if (profilePicture.isEmpty()) {
      return member;
    }

    return member.toBuilder().images(List.of(profilePicture.get())).build();
  }

  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  private Optional<Image> fetchProfilePicture(final Long memberId) {
    try {
      return profilePicRepo
          .findByMemberId(memberId)
          .map(MemberProfilePicture::getResource)
          .map(this::convertResourceToImage);
    } catch (Exception e) {
      // Catching generic exception intentionally to ensure profile picture fetch
      // failures don't break the entire member retrieval operation
      log.warn("Failed to fetch profile picture for member {}: {}", memberId, e.getMessage());
      return Optional.empty();
    }
  }

  private Image convertResourceToImage(final Resource resource) {
    return new Image(
        resource.getDriveFileLink(),
        resource.getName().isEmpty() ? "Profile picture" : resource.getName(),
        ImageType.DESKTOP);
  }
}
