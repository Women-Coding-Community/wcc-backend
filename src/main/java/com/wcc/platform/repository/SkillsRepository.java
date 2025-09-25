package com.wcc.platform.repository;

import com.wcc.platform.domain.platform.mentorship.Skills;

/** Repository interface for managing skills entities. */
public interface SkillsRepository {
  /**
   * Return mentor skills like technical areas, languages, experience
   *
   * @param mentorId id of the mentor
   * @return Skills
   */
  Skills findByMentorId(Long mentorId);
}
