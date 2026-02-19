package com.wcc.platform.service;

import static com.wcc.platform.factories.SetupFactories.createMemberDtoTest;
import static com.wcc.platform.factories.SetupFactories.createMemberTest;
import static com.wcc.platform.factories.SetupFactories.createUpdatedMemberTest;
import static com.wcc.platform.factories.SetupMentorFactories.createMemberProfilePictureTest;
import static com.wcc.platform.factories.SetupMentorFactories.createResourceTest;
import static com.wcc.platform.factories.SetupUserAccountFactories.createUserAccountTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.auth.UserAccount;
import com.wcc.platform.domain.cms.attributes.ImageType;
import com.wcc.platform.domain.exceptions.DuplicatedMemberException;
import com.wcc.platform.domain.exceptions.MemberNotFoundException;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.member.MemberDto;
import com.wcc.platform.domain.platform.type.MemberType;
import com.wcc.platform.domain.platform.type.RoleType;
import com.wcc.platform.repository.MemberProfilePictureRepository;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.UserAccountRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class MemberServiceTest {

  @Mock private MemberRepository memberRepository;
  @Mock private UserAccountRepository userRepository;
  @Mock private MemberProfilePictureRepository profilePicRepo;
  @Mock private UserProvisionService userProvisionService;

  private MemberService service;

  private Member member;
  private Member updatedMember;
  private MemberDto memberDto;
  private UserAccount user;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    service =
        new MemberService(memberRepository, userRepository, profilePicRepo, userProvisionService);
    member = createMemberTest(MemberType.DIRECTOR);
    memberDto = createMemberDtoTest(MemberType.DIRECTOR);
    updatedMember = createUpdatedMemberTest(member, memberDto);
    user = createUserAccountTest(member);
  }

  @Test
  @DisplayName(
      "Given Member, when created, then should return created member and create user account")
  void testCreateMember() {
    when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.empty());
    when(userRepository.findByEmail(member.getEmail())).thenReturn(Optional.empty());
    when(memberRepository.create(any(Member.class))).thenReturn(member);

    Member result = service.createMember(member);

    assertEquals(member, result);
    verify(memberRepository).create(member);
    verify(userProvisionService)
        .provisionUserRole(eq(result.getId()), eq(result.getEmail()), eq(RoleType.VIEWER));
  }

  @Test
  @DisplayName(
      "Given Member email exist When try create Then should throws DuplicatedMemberException")
  void testCreateMemberDuplicated() {
    when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));

    assertThrows(DuplicatedMemberException.class, () -> service.createMember(member));

    verify(memberRepository, never()).create(any());
  }

  @Test
  @DisplayName("Given has members When getting all members, then should return all")
  void testGetAllMembers() {
    List<Member> members = List.of(member);
    when(memberRepository.getAll()).thenReturn(members);

    List<Member> result = service.getAllMembers();

    assertEquals(members, result);
    verify(memberRepository).getAll();
  }

  @Test
  @DisplayName("Given no member exist When getting all members, then should return empty list")
  void testGetAllMembersEmpty() {
    when(memberRepository.getAll()).thenReturn(null);

    List<Member> result = service.getAllMembers();

    assertTrue(result.isEmpty());
    verify(memberRepository).getAll();
  }

  @Test
  @DisplayName(
      "Given member exist When updating the member "
          + "Then should update member attributes and return updated member")
  void testUpdateMember() {
    long memberId = 1L;
    when(memberRepository.update(anyLong(), any())).thenReturn(updatedMember);
    when(memberRepository.findById(memberId)).thenReturn(Optional.ofNullable(member));
    Member result = service.updateMember(memberId, memberDto);

    assertEquals(updatedMember, result);
    verify(memberRepository).update(anyLong(), any());
  }

  @Test
  @DisplayName(
      "Given member exist When updating the member with incorrect id object "
          + "Then throws IllegalArgumentException ")
  void testUpdateMemberIllegalId() {
    long memberId = 2L;

    assertThrows(IllegalArgumentException.class, () -> service.updateMember(memberId, memberDto));

    verify(memberRepository, never()).findById(any());
    verify(memberRepository, never()).create(any());
  }

  @Test
  @DisplayName(
      "Given member does not exist When try to delete member "
          + "Then throws MemberNotFoundException ")
  void deleteUserThrowsException() {
    Long memberId = memberDto.getId();
    when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

    assertThrows(MemberNotFoundException.class, () -> service.deleteMember(memberId));

    verify(memberRepository, never()).deleteById(any());
  }

  @Test
  @DisplayName("Given member exist When try to delete member Then user is deleted")
  void deleteUserSuccess() {
    Long memberId = memberDto.getId();
    when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

    service.deleteMember(memberId);

    verify(memberRepository, times(1)).deleteById(memberId);
  }

  @Test
  @DisplayName(
      "Given member with profile picture, when getAllMembers is called, then images list should"
          + " contain profile picture")
  void shouldMergeProfilePictureIntoImagesWhenMemberHasProfilePicture() {
    when(memberRepository.getAll()).thenReturn(List.of(member));

    var resource = createResourceTest();
    var profilePicture =
        createMemberProfilePictureTest(member.getId()).toBuilder().resource(resource).build();
    when(profilePicRepo.findByMemberId(member.getId())).thenReturn(Optional.of(profilePicture));

    var result = service.getAllMembers();

    assertThat(result).hasSize(1);
    var memberResult = result.get(0);
    assertThat(memberResult.getImages()).hasSize(1);
    assertThat(memberResult.getImages().get(0).path()).isEqualTo(resource.getDriveFileLink());
    assertThat(memberResult.getImages().get(0).type()).isEqualTo(ImageType.DESKTOP);
  }

  @Test
  @DisplayName(
      "Given member without profile picture, when getAllMembers is called, then images list should"
          + " remain unchanged from original member")
  void shouldReturnOriginalImagesWhenMemberHasNoProfilePicture() {
    when(memberRepository.getAll()).thenReturn(List.of(member));
    when(profilePicRepo.findByMemberId(member.getId())).thenReturn(Optional.empty());

    var result = service.getAllMembers();

    assertThat(result).hasSize(1);
    var memberResult = result.get(0);
    assertThat(memberResult.getImages()).isEqualTo(member.getImages());
  }

  @Test
  @DisplayName(
      "Given profile picture fetch throws exception, when getAllMembers is called, then images"
          + " should remain unchanged and exception should be logged")
  void shouldHandleExceptionWhenFetchingProfilePictureFails() {
    when(memberRepository.getAll()).thenReturn(List.of(member));
    when(profilePicRepo.findByMemberId(member.getId()))
        .thenThrow(new RuntimeException("Database error"));

    var result = service.getAllMembers();

    assertThat(result).hasSize(1);
    var memberResult = result.get(0);
    assertThat(memberResult.getImages()).isEqualTo(member.getImages());
  }

  @Test
  void shouldNotCreateUserAccountWhenUserAlreadyExists() {
    UserAccount existingUserAccount = new UserAccount(999L, member.getEmail(), RoleType.ADMIN);

    when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.empty());
    when(userRepository.findByEmail(member.getEmail()))
        .thenReturn(Optional.of(existingUserAccount));
    when(memberRepository.create(member)).thenReturn(member);

    service.createMember(member);

    verify(userRepository, never()).create(any(UserAccount.class));
  }
}
