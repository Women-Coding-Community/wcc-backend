package com.wcc.platform.service;

import static com.wcc.platform.factories.SetupFactories.createMemberDtoTest;
import static com.wcc.platform.factories.SetupFactories.createMemberTest;
import static com.wcc.platform.factories.SetupFactories.createUpdatedMemberTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.platform.Member;
import com.wcc.platform.domain.platform.MemberDto;
import com.wcc.platform.domain.platform.MemberType;
import com.wcc.platform.domain.platform.ResourceContent;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.ResourceContentRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PlatformServiceTest {

  @Mock private ResourceContentRepository resourceContentRepository;
  @Mock private MemberRepository memberRepository;

  @InjectMocks private PlatformService service;

  private ResourceContent resourceContent;
  private Member member;
  private Member updatedMember;
  private MemberDto memberDto;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    resourceContent = new ResourceContent();
    member = createMemberTest(MemberType.DIRECTOR);
    memberDto = createMemberDtoTest(MemberType.COLLABORATOR);
    updatedMember = createUpdatedMemberTest(member, memberDto);
  }

  @Test
  @DisplayName("Given ResourceContent, when saved, then should return saved resource content")
  void testSaveResourceContent() {
    when(resourceContentRepository.create(any(ResourceContent.class))).thenReturn(resourceContent);

    ResourceContent result = service.saveResourceContent(resourceContent);

    assertEquals(resourceContent, result);
    verify(resourceContentRepository).create(resourceContent);
  }

  @Test
  @DisplayName("Given valid id, when getting resource by id, then should return resource content")
  void testGetResourceById() {
    when(resourceContentRepository.findById("1")).thenReturn(Optional.of(resourceContent));

    ResourceContent result = service.getResourceById("1");

    assertEquals(resourceContent, result);
    verify(resourceContentRepository).findById("1");
  }

  @Test
  @DisplayName(
      "Given invalid id, when getting resource by id, then should throw ContentNotFoundException")
  void testGetResourceByIdNotFound() {
    when(resourceContentRepository.findById("1")).thenReturn(Optional.empty());

    assertThrows(ContentNotFoundException.class, () -> service.getResourceById("1"));
  }

  @Test
  @DisplayName("Given valid id, when deleting resource by id, then should delete the resource")
  void testDeleteById() {
    when(resourceContentRepository.findById("1")).thenReturn(Optional.of(resourceContent));

    service.deleteById("1");

    verify(resourceContentRepository).deleteById(resourceContent.getId());
  }

  @Test
  @DisplayName(
      "Given invalid id, when deleting resource by id, then should throw ContentNotFoundException")
  void testDeleteByIdNotFound() {
    when(resourceContentRepository.findById("1")).thenReturn(Optional.empty());

    assertThrows(ContentNotFoundException.class, () -> service.deleteById("1"));
  }

  @Test
  @DisplayName("Given Member, when created, then should return created member")
  void testCreateMember() {
    when(memberRepository.save(any(Member.class))).thenReturn(member);

    Member result = service.createMember(member);

    assertEquals(member, result);
    verify(memberRepository).save(member);
  }

  @Test
  @DisplayName("When getting all members, then should return list of members")
  void testGetAllMembers() {
    List<Member> members = List.of(member);
    when(memberRepository.getAll()).thenReturn(members);

    List<Member> result = service.getAll();

    assertEquals(members, result);
    verify(memberRepository).getAll();
  }

  @Test
  @DisplayName("When getting all members and none exist, then should return empty list")
  void testGetAllMembersEmpty() {
    when(memberRepository.getAll()).thenReturn(null);

    List<Member> result = service.getAll();

    assertTrue(result.isEmpty());
    verify(memberRepository).getAll();
  }

  @Test
  @DisplayName(
      "When updating the member with memberDto, then should return the member with "
          + "updated data from memberDto")
  void testUpdateMember() {
    when(memberRepository.update(updatedMember)).thenReturn(updatedMember);
    when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.ofNullable(member));
    Member result = service.updateMember(member.getEmail(), memberDto);

    assertEquals(updatedMember, result);
    verify(memberRepository).update(updatedMember);
  }
}
