package com.wcc.platform.repository;

import com.wcc.platform.domain.cms.pages.mentorship.MenteeSection;
import java.util.Optional;

/** Repository interface for retrieving MenteeSection data related to a mentor. */
public interface MenteeSectionRepository {

  /**
   * Find the mentee section associated with the given mentor id.
   *
   * @param mentorId the id of the mentor
   * @return an {@link Optional} containing the found {@link MenteeSection}, or empty if none found
   */
  Optional<MenteeSection> findByMentorId(final long mentorId);
}
