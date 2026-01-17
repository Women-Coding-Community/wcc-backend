package com.wcc.platform.repository;

import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import java.util.List;

/**
 * Repository interface for managing mentees entities. Provides methods to perform CRUD operations
 * and additional mentee-related queries on the data source.
 */
public interface MenteeRepository extends CrudRepository<Mentee, Long> {

    /**
     * Return all saved mentees.
     *
     * @return list of mentees
     */
    List<Mentee> getAll();

    /**
     * Create a mentee for a specific cycle year.
     *
     * @param mentee The mentee to create
     * @param cycleYear The year of the mentorship cycle
     * @return The created mentee
     */
    Mentee create(Mentee mentee, Integer cycleYear);

    /**
     * Check if a mentee is already registered for a specific year and mentorship type.
     *
     * @param menteeId The mentee ID
     * @param cycleYear The year of the cycle
     * @param mentorshipType The mentorship type
     * @return true if mentee is registered, false otherwise
     */
    boolean existsByMenteeYearType(
        Long menteeId, Integer cycleYear, MentorshipType mentorshipType);
}
