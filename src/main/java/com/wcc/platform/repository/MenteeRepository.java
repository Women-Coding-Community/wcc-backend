package com.wcc.platform.repository;

import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.mentorship.Mentee;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing mentees entities. Provides methods to perform CRUD operations
 * and additional mentee-related queries on the data source.
 */
public interface MenteeRepository extends CrudRepository<Mentee, Long> {

}
