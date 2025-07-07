package com.wcc.platform.repository;

import com.wcc.platform.domain.platform.Member;
import java.util.List;
import java.util.Optional;

public interface MembersRepository extends CrudRepository<Member, Long> {
  /**
   * Find member by email.
   *
   * @param email member's email
   * @return Optional with Member object or empty Optional
   */
  Optional<Member> findByEmail(String email);

  /**
   * Return all saved members.
   *
   * @return list of members
   */
  List<Member> getAll();

  /**
   * Return member's Id.
   *
   * @return member's Id in database
   */
  Long findIdByEmail(String email);
}
