package com.wcc.platform.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.exceptions.MemberNotFoundException;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.type.MemberType;
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
import org.springframework.test.context.ActiveProfiles;

/** Integration tests for ResourceService external profile picture feature. */
@ActiveProfiles("test")
class ResourceServiceIntegrationTest extends DefaultDatabaseSetup {

  private static final String TEST_EMAIL = "resource-integration-test@wcc.com";
  private static final String EXTERNAL_URL = "https://example.com/photo.jpg";

  @Autowired private ResourceService resourceService;
  @Autowired private MemberRepository memberRepository;
  @Autowired private MemberProfilePictureRepository profilePicRepo;
  @Autowired private ResourceRepository resourceRepository;

  private Long memberId;

  @BeforeEach
  void setUp() {
    memberRepository.deleteByEmail(TEST_EMAIL);
    var member =
        Member.builder()
            .fullName("Test Member")
            .position("Engineer")
            .email(TEST_EMAIL)
            .slackDisplayName("testmember")
            .country(new Country("GB", "United Kingdom"))
            .memberTypes(List.of(MemberType.MEMBER))
            .isWomen(true)
            .build();
    memberId = memberRepository.create(member).getId();
  }

  @AfterEach
  void tearDown() {
    profilePicRepo
        .findByMemberId(memberId)
        .ifPresent(
            pic -> {
              profilePicRepo.deleteByMemberId(memberId);
              resourceRepository.deleteById(pic.getResourceId());
            });
    memberRepository.deleteByEmail(TEST_EMAIL);
  }

  @Test
  @DisplayName(
      "Given valid member and URL, "
          + "when saving external profile picture, "
          + "then stores profile picture")
  void shouldSaveExternalProfilePictureForValidMember() {
    var result = resourceService.saveExternalProfilePicture(memberId, EXTERNAL_URL);

    assertThat(result).isNotNull();
    assertThat(result.getMemberId()).isEqualTo(memberId);
    assertThat(result.getResource()).isNotNull();
    assertThat(result.getResource().getDriveFileLink()).isEqualTo(EXTERNAL_URL);
    assertThat(result.getResource().getDriveFileId()).isNull();
  }

  @Test
  @DisplayName(
      "Given member with existing picture, "
          + "when saving external profile picture, "
          + "then replaces previous picture")
  void shouldReplaceExistingProfilePictureWithExternalUrl() {
    resourceService.saveExternalProfilePicture(memberId, EXTERNAL_URL);

    var newUrl = "https://example.com/new-photo.jpg";
    var result = resourceService.saveExternalProfilePicture(memberId, newUrl);

    assertThat(result.getResource().getDriveFileLink()).isEqualTo(newUrl);
    assertThat(profilePicRepo.findByMemberId(memberId)).isPresent();
  }

  @Test
  @DisplayName(
      "Given non-existent member, "
          + "when saving external profile picture, "
          + "then throws MemberNotFoundException")
  void shouldThrowMemberNotFoundWhenMemberDoesNotExist() {
    assertThatThrownBy(() -> resourceService.saveExternalProfilePicture(999_999L, EXTERNAL_URL))
        .isInstanceOf(MemberNotFoundException.class);
  }
}
