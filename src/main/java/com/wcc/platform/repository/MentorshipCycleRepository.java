package com.wcc.platform.repository;

import com.wcc.platform.domain.platform.mentorship.CycleStatus;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleEntity;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing mentorship cycles.
 * Provides methods to query and manage mentorship cycle configuration.
 */
public interface MentorshipCycleRepository extends CrudRepository<MentorshipCycleEntity, Long> {

    /**
     * Find the currently open cycle for registration.
     *
     * @return Optional containing the open cycle, or empty if no cycle is open
     */
    Optional<MentorshipCycleEntity> findOpenCycle();

    /**
     * Find a cycle by year and mentorship type.
     *
     * @param year the cycle year
     * @param type the mentorship type
     * @return Optional containing the matching cycle
     */
    Optional<MentorshipCycleEntity> findByYearAndType(Integer year, MentorshipType type);

    /**
     * Find all cycles with a specific status.
     *
     * @param status the cycle status
     * @return list of cycles with the given status
     */
    List<MentorshipCycleEntity> findByStatus(CycleStatus status);

    /**
     * Find all cycles for a specific year.
     *
     * @param year the cycle year
     * @return list of cycles in that year
     */
    List<MentorshipCycleEntity> findByYear(Integer year);

    /**
     * Get all mentorship cycles.
     *
     * @return list of all cycles
     */
    List<MentorshipCycleEntity> getAll();
}
