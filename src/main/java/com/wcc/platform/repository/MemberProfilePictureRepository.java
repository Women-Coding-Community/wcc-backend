package com.wcc.platform.repository;

import com.wcc.platform.domain.resource.MemberProfilePicture;
import java.util.Optional;
import java.util.UUID;

/** Repository interface for managing Member's profile picture. */
public interface MemberProfilePictureRepository extends CrudRepository<MemberProfilePicture, UUID> {

  /**
   * Find a Member's profile picture by email.
   *
   * @param memberId the mentor's email
   * @return an Optional containing the mentor's profile picture, or empty if not found
   */
  Optional<MemberProfilePicture> findByMemberId(Integer memberId);

  /**
   * Delete Member profile picture by the associated member ID.
   *
   * @param memberId the ID of the resource
   */
  void deleteByMemberId(Integer memberId);
}
