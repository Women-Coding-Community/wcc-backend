package com.wcc.platform.repository;

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
}
