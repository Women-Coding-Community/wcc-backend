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
}
