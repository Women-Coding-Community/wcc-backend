package com.wcc.platform.repository;

import com.wcc.platform.domain.platform.mentorship.Skills;
import java.util.Optional;

/** Repository interface for managing skills entities. */
public interface SkillRepository {
  /**
   * Return mentor skills like technical areas, languages, experience
   *
   * @param mentorId id of the mentor
   * @return Skills
   */
  Optional<Skills> findSkills(Long mentorId);
}
