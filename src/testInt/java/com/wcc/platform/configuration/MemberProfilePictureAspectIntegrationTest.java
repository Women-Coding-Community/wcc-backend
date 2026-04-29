package com.wcc.platform.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.attributes.ImageType;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.type.MemberType;
import com.wcc.platform.domain.resource.MemberProfilePicture;
import com.wcc.platform.repository.MemberProfilePictureRepository;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.ResourceRepository;
import com.wcc.platform.repository.postgres.DefaultDatabaseSetup;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Integration test to verify that the MemberProfilePictureAspect correctly intercepts member
 * creation and automatically saves profile pictures to the database.
 */
class MemberProfilePictureAspectIntegrationTest extends DefaultDatabaseSetup {

  @Autowired private MemberRepository memberRepository;
  @Autowired private ResourceRepository resourceRepository;
  @Autowired private MemberProfilePictureRepository profilePicRepo;

  private Member testMember;

  @BeforeEach
  void setUp() {
    testMember =
        Member.builder()
            .fullName("Test Member With Image")
            .position("Software Engineer")
            .email("aspect-test-member@example.com")
            .slackDisplayName("AspectTestMember")
            .country(new Country("US", "United States"))
            .city("San Francisco")
            .companyName("Tech Company")
            .memberTypes(List.of(MemberType.MEMBER))
            .images(
                List.of(
                    new Image(
                        "https://example.com/aspect-test-profile.jpg",
                        "Test Profile Picture",
                        ImageType.DESKTOP)))
            .network(List.of())
            .isWomen(true)
            .build();
  }

  @AfterEach
  void tearDown() {
    if (testMember != null && testMember.getId() != null) {

      profilePicRepo
          .findByMemberId(testMember.getId())
          .ifPresent(
              pic -> {
                profilePicRepo.deleteByMemberId(testMember.getId());
                resourceRepository.deleteById(pic.getResourceId());
              });

      memberRepository.deleteById(testMember.getId());
    }
  }

  @Test
  @DisplayName(
      "Given member with images, when created via repository, then aspect saves profile picture"
          + " automatically")
  void testAspectSavesProfilePictureOnMemberCreation() {
    final Member createdMember = memberRepository.create(testMember);

    assertThat(createdMember).isNotNull();
    assertThat(createdMember.getId()).isNotNull();

    final var profilePicture = profilePicRepo.findByMemberId(createdMember.getId());
    assertThat(profilePicture).isPresent();

    final MemberProfilePicture savedPic = profilePicture.get();
    assertThat(savedPic.getMemberId()).isEqualTo(createdMember.getId());
    assertThat(savedPic.getResource()).isNotNull();
    assertThat(savedPic.getResource().getDriveFileLink())
        .isEqualTo("https://example.com/aspect-test-profile.jpg");
    assertThat(savedPic.getResource().getName()).isEqualTo("Test Profile Picture");

    testMember = createdMember;
  }

  @Test
  @DisplayName(
      "Given member without images, when created via repository, then aspect does not save profile"
          + " picture")
  void testAspectDoesNotSaveProfilePictureWhenNoImages() {
    final Member memberWithoutImages = testMember.toBuilder().images(List.of()).build();
    final Member createdMember = memberRepository.create(memberWithoutImages);

    assertThat(createdMember).isNotNull();
    assertThat(createdMember.getId()).isNotNull();

    final var profilePicture = profilePicRepo.findByMemberId(createdMember.getId());
    assertThat(profilePicture).isEmpty();

    testMember = createdMember;
  }

  @Test
  @DisplayName(
      "Given member with new images, when updated via repository, then aspect updates profile"
          + " picture")
  void testAspectUpdatesProfilePictureOnMemberUpdate() {
    final Member createdMember = memberRepository.create(testMember);
    assertThat(createdMember.getId()).isNotNull();

    var initialPic = profilePicRepo.findByMemberId(createdMember.getId());
    assertThat(initialPic).isPresent();
    assertThat(initialPic.get().getResource().getDriveFileLink())
        .isEqualTo("https://example.com/aspect-test-profile.jpg");

    final Member updatedMember =
        createdMember.toBuilder()
            .images(
                List.of(
                    new Image(
                        "https://example.com/updated-profile.jpg",
                        "Updated Profile Picture",
                        ImageType.DESKTOP)))
            .build();

    memberRepository.update(createdMember.getId(), updatedMember);

    final var updatedPic = profilePicRepo.findByMemberId(createdMember.getId());
    assertThat(updatedPic).isPresent();
    assertThat(updatedPic.get().getResource().getDriveFileLink())
        .isEqualTo("https://example.com/updated-profile.jpg");
    assertThat(updatedPic.get().getResource().getName()).isEqualTo("Updated Profile Picture");

    testMember = createdMember;
  }
}
