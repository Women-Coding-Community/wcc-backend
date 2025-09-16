package com.wcc.platform.service;

import static com.wcc.platform.factories.SetupFactories.createMemberDtoTest;
import static com.wcc.platform.factories.SetupFactories.createMemberTest;
import static com.wcc.platform.factories.SetupFactories.createUpdatedMemberTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.member.MemberDto;
import com.wcc.platform.domain.platform.type.MemberType;
import com.wcc.platform.repository.MembersRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PlatformServiceTest {

  @Mock private MembersRepository membersRepository;

  @InjectMocks private PlatformService service;

  private Member member;
  private Member updatedMember;
  private MemberDto memberDto;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    member = createMemberTest(MemberType.DIRECTOR);
    memberDto = createMemberDtoTest(MemberType.COLLABORATOR);
    updatedMember = createUpdatedMemberTest(member, memberDto);
  }

  @Test
  @DisplayName("Given Member, when created, then should return created member")
  void testCreateMember() {
    when(membersRepository.create(any(Member.class))).thenReturn(member);

    Member result = service.createMember(member);

    assertEquals(member, result);
    verify(membersRepository).create(member);
  }

  @Test
  @DisplayName("When getting all members, then should return list of members")
  void testGetAllMembers() {
    List<Member> members = List.of(member);
    when(membersRepository.getAll()).thenReturn(members);

    List<Member> result = service.getAllMembers();

    assertEquals(members, result);
    verify(membersRepository).getAll();
  }

  @Test
  @DisplayName("When getting all members and none exist, then should return empty list")
  void testGetAllMembersEmpty() {
    when(membersRepository.getAll()).thenReturn(null);

    List<Member> result = service.getAllMembers();

    assertTrue(result.isEmpty());
    verify(membersRepository).getAll();
  }

  @Test
  @DisplayName(
      "When updating the member with memberDto, then should return the member with "
          + "updated data from memberDto")
  void testUpdateMember() {
    when(membersRepository.update(1L, updatedMember)).thenReturn(updatedMember);
    when(membersRepository.findByEmail(member.getEmail())).thenReturn(Optional.ofNullable(member));
    when(membersRepository.findIdByEmail(member.getEmail())).thenReturn(1L);
    Member result = service.updateMember(member.getEmail(), memberDto);

    assertEquals(updatedMember, result);
    verify(membersRepository).update(1L, updatedMember);
  }
}
