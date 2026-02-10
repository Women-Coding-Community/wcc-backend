package com.wcc.platform.repository;

import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import java.util.List;
import java.util.Optional;

public interface MentorRepository extends CrudRepository<Mentor, Long> {
  /**
   * Find mentor by email.
   *
   * @param email mentor's email
   * @return Optional with Mentor object or empty Optional
   */
  Optional<Mentor> findByEmail(String email);

  /**
   * Return all mentors.
   *
   * @return list of mentors
   */
  List<Mentor> getAll();

  /**
   * Return mentor's Id.
   *
   * @return mentor's Id
   */
  Long findIdByEmail(String email);

  /** Update mentor profile status. */
  Mentor updateProfileStatus(final Long mentorId, final ProfileStatus profileStatus);
}
