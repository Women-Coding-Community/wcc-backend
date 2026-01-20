package com.wcc.platform.repository;

import com.wcc.platform.domain.platform.mentorship.MentorshipMatch;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing confirmed mentorship matches.
 * Tracks mentor-mentee pairings throughout their lifecycle.
 */
public interface MentorshipMatchRepository extends CrudRepository<MentorshipMatch, Long> {

    /**
     * Find all active matches for a specific mentor.
     *
     * @param mentorId the mentor ID
     * @return list of active mentee matches
     */
    List<MentorshipMatch> findActiveMenteesByMentor(Long mentorId);

    /**
     * Find the active mentor for a specific mentee.
     *
     * @param menteeId the mentee ID
     * @return Optional containing the active match
     */
    Optional<MentorshipMatch> findActiveMentorByMentee(Long menteeId);

    /**
     * Find all matches in a specific cycle.
     *
     * @param cycleId the cycle ID
     * @return list of matches in this cycle
     */
    List<MentorshipMatch> findByCycle(Long cycleId);

    /**
     * Count active mentees for a mentor in a specific cycle.
     *
     * @param mentorId the mentor ID
     * @param cycleId the cycle ID
     * @return number of active mentees
     */
    int countActiveMenteesByMentorAndCycle(Long mentorId, Long cycleId);

    /**
     * Check if a mentee is already matched in a specific cycle.
     *
     * @param menteeId the mentee ID
     * @param cycleId the cycle ID
     * @return true if mentee has an active match in this cycle
     */
    boolean isMenteeMatchedInCycle(Long menteeId, Long cycleId);

    /**
     * Find a match by mentor, mentee, and cycle.
     *
     * @param mentorId the mentor ID
     * @param menteeId the mentee ID
     * @param cycleId the cycle ID
     * @return Optional containing the match if found
     */
    Optional<MentorshipMatch> findByMentorMenteeCycle(Long mentorId, Long menteeId, Long cycleId);

    /**
     * Get all mentorship matches.
     *
     * @return list of all matches
     */
    List<MentorshipMatch> getAll();
}
