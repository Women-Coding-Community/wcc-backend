package com.wcc.platform.service;

import static com.wcc.platform.factories.SetupFactories.createMemberTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.platform.Member;
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

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    resourceContent = new ResourceContent();
    member = createMemberTest(MemberType.DIRECTOR);
  }

  @Test
  void whenGetAllGivenValidJson() {
    var member = createMemberTest(MemberType.MEMBER);
    when(memberRepository.getAll()).thenReturn(List.of(member));
    var response = service.getAll();
    assertEquals(List.of(member), response);
  }

  @Test
  @DisplayName("Given ResourceContent, when saved, then should return saved resource content")
  void saveResourceContent() {
    when(resourceContentRepository.save(any(ResourceContent.class))).thenReturn(resourceContent);

    ResourceContent result = service.saveResourceContent(resourceContent);

    assertEquals(resourceContent, result);
    verify(resourceContentRepository).save(resourceContent);
  }

  @Test
  @DisplayName(
      "Given resources exist, when getting all resources, then should return all resources")
  void getAllResources() {
    var resources = List.of(resourceContent);
    when(resourceContentRepository.findAll()).thenReturn(resources);

    var result = service.getAllResources();

    assertEquals(resources, result);
    verify(resourceContentRepository).findAll();
  }

  @Test
  @DisplayName("Given valid id, when getting resource by id, then should return resource content")
  void getResourceById() {
    when(resourceContentRepository.findById("1")).thenReturn(Optional.of(resourceContent));

    ResourceContent result = service.getResourceById("1");

    assertEquals(resourceContent, result);
    verify(resourceContentRepository).findById("1");
  }

  @Test
  @DisplayName(
      "Given invalid id, when getting resource by id, then should throw ContentNotFoundException")
  void getResourceByIdNotFound() {
    when(resourceContentRepository.findById("1")).thenReturn(Optional.empty());

    assertThrows(ContentNotFoundException.class, () -> service.getResourceById("1"));
  }

  @Test
  @DisplayName("Given valid id, when deleting resource by id, then should delete the resource")
  void deleteById() {
    when(resourceContentRepository.findById("1")).thenReturn(Optional.of(resourceContent));

    service.deleteById("1");

    verify(resourceContentRepository).deleteById(resourceContent.getId());
  }

  @Test
  @DisplayName(
      "Given invalid id, when deleting resource by id, then should throw ContentNotFoundException")
  void deleteByIdNotFound() {
    when(resourceContentRepository.findById("1")).thenReturn(Optional.empty());

    assertThrows(ContentNotFoundException.class, () -> service.deleteById("1"));
  }

  @Test
  @DisplayName("Given Member, when created, then should return created member")
  void createMember() {
    when(memberRepository.save(any(Member.class))).thenReturn(member);

    Member result = service.createMember(member);

    assertEquals(member, result);
    verify(memberRepository).save(member);
  }

  @Test
  @DisplayName("When getting all members, then should return list of members")
  void getAllMembers() {
    List<Member> members = List.of(member);
    when(memberRepository.getAll()).thenReturn(members);

    List<Member> result = service.getAll();

    assertEquals(members, result);
    verify(memberRepository).getAll();
  }

  @Test
  @DisplayName("When getting all members and none exist, then should return empty list")
  void getAllMembersEmpty() {
    when(memberRepository.getAll()).thenReturn(null);

    List<Member> result = service.getAll();

    assertTrue(result.isEmpty());
    verify(memberRepository).getAll();
  }
}
