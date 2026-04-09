package com.wcc.platform.repository;

import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.platform.mentorship.Mentee;
import java.util.List;

/**
 * Repository interface for managing mentee applications to mentors. Supports priority-based mentor
 * selection and application workflow tracking.
 */
public interface MenteeRepository extends CrudRepository<Mentee, Long> {

  /**
   * Return all mentees.
   *
   * @return list of mentees
   */
  List<Mentee> getAll();

  /**
   * Return all mentees with the given profile status.
   *
   * @param status the profile status to filter by
   * @return list of mentees with the given status
   */
  List<Mentee> findByStatus(ProfileStatus status);

  /**
   * Update the profile status of a mentee.
   *
   * @param menteeId the mentee ID
   * @param status the new profile status
   * @return the updated mentee
   */
  Mentee updateProfileStatus(Long menteeId, ProfileStatus status);

  /**
   * Return all mentees matching by IDs
   *
   * @param menteeIds
   * @return list of mentees
   */
  List<Mentee> findAllById(List<Long> menteeIds);
}
