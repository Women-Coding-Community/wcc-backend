package com.wcc.platform.repository;

import com.wcc.platform.domain.resource.MentorProfilePicture;
import java.util.Optional;
import java.util.UUID;

/** Repository interface for managing mentor profile pictures. */
public interface MentorProfilePictureRepository extends CrudRepository<MentorProfilePicture, UUID> {

  /**
   * Find a mentor's profile picture by email.
   *
   * @param email the mentor's email
   * @return an Optional containing the mentor's profile picture, or empty if not found
   */
  Optional<MentorProfilePicture> findByMentorEmail(String email);

  /**
   * Delete a mentor's profile picture by email.
   *
   * @param email the mentor's email
   */
  void deleteByMentorEmail(String email);

  /**
   * Find a mentor's profile picture by the associated resource ID.
   *
   * @param resourceId the ID of the resource
   * @return an Optional containing the mentor's profile picture, or empty if not found
   */
  Optional<MentorProfilePicture> findByResourceId(UUID resourceId);

  /**
   * Delete mentor profile picture(s) by the associated resource ID.
   *
   * @param resourceId the ID of the resource
   */
  void deleteByResourceId(UUID resourceId);
}
