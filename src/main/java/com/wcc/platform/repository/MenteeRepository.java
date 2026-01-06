package com.wcc.platform.repository;

import com.wcc.platform.domain.platform.mentorship.Mentee;
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

}
