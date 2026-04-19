package com.wcc.platform.configuration;

import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.type.ResourceType;
import com.wcc.platform.domain.resource.MemberProfilePicture;
import com.wcc.platform.domain.resource.Resource;
import com.wcc.platform.repository.MemberProfilePictureRepository;
import com.wcc.platform.repository.ResourceRepository;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * Aspect that automatically saves member profile pictures after member creation or update.
 *
 * <p>This aspect intercepts calls to {@link
 * com.wcc.platform.repository.postgres.component.MemberMapper#addMember(Member)} and {@link
 * com.wcc.platform.repository.postgres.component.MemberMapper#updateMember(Member, Long)} to
 * automatically persist profile pictures from the member's images list into the
 * member_profile_pictures table.
 *
 * <p>This ensures all member types (Member, Mentor, Mentee) automatically get their profile
 * pictures saved without requiring explicit calls in each service.
 */
@Slf4j
@Aspect
@Component
@AllArgsConstructor
public class MemberProfilePictureAspect {

  private final ResourceRepository resourceRepository;
  private final MemberProfilePictureRepository profilePicRepo;

  /**
   * After a member is created, save their profile picture if images are provided.
   *
   * @param member the member that was created
   * @param memberId the ID of the created member (returned by addMember)
   */
  @AfterReturning(
      pointcut =
          "execution(* com.wcc.platform.repository.postgres.component.MemberMapper.addMember(..)) "
              + "&& args(member)",
      returning = "memberId")
  public void afterMemberCreation(final Member member, final Long memberId) {
    log.debug("Aspect triggered: afterMemberCreation for memberId={}", memberId);
    saveProfilePictureIfPresent(memberId, member.getImages());
  }

  /**
   * After a member is updated, update their profile picture if images have changed.
   *
   * @param member the member that was updated
   * @param memberId the ID of the updated member
   */
  @AfterReturning(
      pointcut =
          "execution(* com.wcc.platform.repository.postgres.component."
              + "MemberMapper.updateMember(..)) && args(member, memberId)")
  public void afterMemberUpdate(final Member member, final Long memberId) {
    log.debug("Aspect triggered: afterMemberUpdate for memberId={}", memberId);
    updateProfilePictureIfChanged(memberId, member.getImages());
  }

  /**
   * Saves the first image from the images list as a profile picture in the profile pictures table.
   *
   * @param memberId the member's identifier
   * @param images the list of images provided in the member creation request
   */
  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  private void saveProfilePictureIfPresent(final Long memberId, final List<Image> images) {
    if (CollectionUtils.isEmpty(images)) {
      return;
    }

    final Image profileImage = images.getFirst();
    if (profileImage == null || StringUtils.isBlank(profileImage.path())) {
      return;
    }

    try {
      final Resource resource =
          Resource.builder()
              .id(UUID.randomUUID())
              .name(StringUtils.defaultIfEmpty(profileImage.alt(), "Profile picture"))
              .description(StringUtils.EMPTY)
              .fileName(StringUtils.EMPTY)
              .contentType(StringUtils.EMPTY)
              .size(0L)
              .driveFileLink(profileImage.path())
              .resourceType(ResourceType.PROFILE_PICTURE)
              .build();

      final Resource savedResource = resourceRepository.create(resource);

      final MemberProfilePicture profilePicture =
          MemberProfilePicture.builder()
              .memberId(memberId)
              .resourceId(savedResource.getId())
              .resource(savedResource)
              .build();

      profilePicRepo.create(profilePicture);
      log.info("Successfully saved profile picture for member {}", memberId);
    } catch (Exception e) {
      // Log error but don't fail member creation if profile picture save fails
      log.error("Failed to save profile picture for member {}: {}", memberId, e.getMessage(), e);
    }
  }

  /**
   * Updates the profile picture if the images have changed.
   *
   * @param memberId the member's identifier
   * @param images the updated list of images
   */
  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  private void updateProfilePictureIfChanged(final Long memberId, final List<Image> images) {
    if (CollectionUtils.isEmpty(images)) {
      return;
    }

    final Image newProfileImage = images.getFirst();
    if (newProfileImage == null || StringUtils.isBlank(newProfileImage.path())) {
      return;
    }

    try {
      // Check if profile picture already exists
      final var existingPicture = profilePicRepo.findByMemberId(memberId);

      if (existingPicture.isPresent()) {
        final String existingUrl =
            existingPicture.get().getResource() != null
                ? existingPicture.get().getResource().getDriveFileLink()
                : null;

        // Only update if URL has changed
        if (newProfileImage.path().equals(existingUrl)) {
          log.debug("Profile picture URL unchanged for member {}, skipping update", memberId);
          return;
        }

        // Delete old profile picture
        deleteExistingProfilePicture(memberId);
      }

      // Save new profile picture
      saveProfilePictureIfPresent(memberId, images);
    } catch (Exception e) {
      log.error("Failed to update profile picture for member {}: {}", memberId, e.getMessage(), e);
    }
  }

  /**
   * Deletes the existing profile picture for a member.
   *
   * @param memberId the member's identifier
   */
  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  private void deleteExistingProfilePicture(final Long memberId) {
    try {
      final var existingPicture = profilePicRepo.findByMemberId(memberId);
      if (existingPicture.isPresent()) {
        final UUID resourceId = existingPicture.get().getResourceId();
        profilePicRepo.deleteByMemberId(memberId);
        resourceRepository.deleteById(resourceId);
        log.info("Deleted existing profile picture for member {}", memberId);
      }
    } catch (Exception e) {
      // Profile picture deletion failures should not break the entire member update operation
      log.warn(
          "Failed to delete existing profile picture for member {}: {}", memberId, e.getMessage());
    }
  }
}
