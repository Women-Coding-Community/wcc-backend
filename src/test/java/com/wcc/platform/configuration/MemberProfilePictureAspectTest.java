package com.wcc.platform.configuration;

import static com.wcc.platform.factories.SetupFactories.createMemberTest;
import static com.wcc.platform.factories.SetupMentorFactories.createResourceTest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.attributes.ImageType;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.type.MemberType;
import com.wcc.platform.domain.resource.Resource;
import com.wcc.platform.repository.MemberProfilePictureRepository;
import com.wcc.platform.repository.ResourceRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class MemberProfilePictureAspectTest {

  @Mock private ResourceRepository resourceRepository;
  @Mock private MemberProfilePictureRepository profilePicRepo;

  private MemberProfilePictureAspect profilePictureAspect;

  private Member member;
  private Resource resource;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    profilePictureAspect = new MemberProfilePictureAspect(resourceRepository, profilePicRepo);
    member = createMemberTest(MemberType.DIRECTOR);
    resource = createResourceTest();
  }

  @Test
  @DisplayName(
      "Given member with images, when afterMemberCreation is called, then profile picture is"
          + " saved")
  void testWhenMemberImagesExist() {
    final var imageUrl = "https://example.com/profile.jpg";
    final var image = new Image(imageUrl, "Profile picture", ImageType.DESKTOP);
    final var memberWithImage = member.toBuilder().images(List.of(image)).build();

    when(resourceRepository.create(any())).thenReturn(resource);

    profilePictureAspect.afterMemberCreation(memberWithImage, 1L);

    verify(resourceRepository).create(any());
    verify(profilePicRepo).create(any());
  }

  @Test
  @DisplayName(
      "Given member without images, when afterMemberCreation is called, then profile picture is"
          + " not saved")
  void testWhenMemberImagesAreEmptyList() {
    final var memberWithoutImages = member.toBuilder().images(List.of()).build();

    profilePictureAspect.afterMemberCreation(memberWithoutImages, 1L);

    verify(resourceRepository, never()).create(any());
    verify(profilePicRepo, never()).create(any());
  }

  @Test
  @DisplayName(
      "Given member with null images, when afterMemberCreation is called, then profile picture is"
          + " not saved")
  void testWhenMemberImagesAreNull() {
    final var memberWithNullImages = member.toBuilder().images(null).build();

    profilePictureAspect.afterMemberCreation(memberWithNullImages, 1L);

    verify(resourceRepository, never()).create(any());
    verify(profilePicRepo, never()).create(any());
  }

  @Test
  @DisplayName(
      "Given member with empty image URL, when afterMemberCreation is called, then profile picture"
          + " is not saved")
  void testWhenMemberImagesWithEmptyUrl() {
    final var image = new Image("", "Profile picture", ImageType.DESKTOP);
    final var memberWithEmptyUrl = member.toBuilder().images(List.of(image)).build();

    profilePictureAspect.afterMemberCreation(memberWithEmptyUrl, 1L);

    verify(resourceRepository, never()).create(any());
    verify(profilePicRepo, never()).create(any());
  }

  @Test
  @DisplayName(
      "Given member with new images, when afterMemberUpdate is called, then profile picture is"
          + " updated")
  void testWhenMemberImagesUpdated() {
    final var imageUrl = "https://example.com/new-profile.jpg";
    final var image = new Image(imageUrl, "New Profile picture", ImageType.DESKTOP);
    final var memberWithImage = member.toBuilder().images(List.of(image)).build();

    when(profilePicRepo.findByMemberId(1L)).thenReturn(Optional.empty());
    when(resourceRepository.create(any())).thenReturn(resource);

    profilePictureAspect.afterMemberUpdate(memberWithImage, 1L);

    verify(resourceRepository).create(any());
    verify(profilePicRepo).create(any());
  }

  @Test
  @DisplayName(
      "Given member with no images, when afterMemberUpdate is called, then nothing happens")
  void testWhenMemberImagesUpdatedWithEmptyList() {
    final var memberWithoutImages = member.toBuilder().images(List.of()).build();

    profilePictureAspect.afterMemberUpdate(memberWithoutImages, 1L);

    verify(profilePicRepo, never()).findByMemberId(any());
    verify(profilePicRepo, never()).deleteByMemberId(any());
    verify(resourceRepository, never()).deleteById(any());
  }

  @Test
  @DisplayName(
      "Given aspect save fails, when afterMemberCreation is called, then exception is caught and"
          + " logged")
  void testWhenErrorIsThrown() {
    final var imageUrl = "https://example.com/profile.jpg";
    final var image = new Image(imageUrl, "Profile picture", ImageType.DESKTOP);
    final var memberWithImage = member.toBuilder().images(List.of(image)).build();

    when(resourceRepository.create(any())).thenThrow(new RuntimeException("Database error"));

    // Should not throw exception
    profilePictureAspect.afterMemberCreation(memberWithImage, 1L);

    verify(resourceRepository).create(any());
    verify(profilePicRepo, never()).create(any());
  }
}
