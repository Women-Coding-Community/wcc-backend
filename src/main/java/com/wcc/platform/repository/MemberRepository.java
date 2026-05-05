package com.wcc.platform.repository;

import com.wcc.platform.domain.platform.member.Member;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing member entities. Provides methods to perform CRUD operations
 * and additional member-related queries on the data source.
 */
public interface MemberRepository extends CrudRepository<Member, Long> {
  /**
   * Find member by email.
   *
   * @param email member's email
   * @return Optional with Member object or empty Optional
   */
  Optional<Member> findByEmail(String email);

  /**
   * Find member by email.
   *
   * @param memberIds list of member ids
   * @return list of emails corresponding to the provided member ids
   * @throws com.wcc.platform.domain.exceptions.MemberNotFoundException if any member id does not
   *     exist in the database
   */
  List<String> findEmails(List<Long> memberIds);

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

  /**
   * Delete member by email.
   *
   * @param email member's email
   */
  void deleteByEmail(String email);

  /**
   * Returns whether a member with the given ID exists in the data source.
   *
   * @param memberId the member's unique identifier
   * @return {@code true} if a member with this ID exists, {@code false} otherwise
   */
  boolean existsById(Long memberId);
}
