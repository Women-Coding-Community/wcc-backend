package com.wcc.platform.repository;

import com.wcc.platform.domain.platform.member.Member;
import java.util.List;
import java.util.Optional;

/** Interface to save and retrieve member's data to and from a file repository. */
public interface MemberRepository {

  /**
   * Save a new member.
   *
   * @param member member to be saved to file
   * @return member
   */
  Member save(Member member);

  /**
   * Update an existing member.
   *
   * @param updatedMember member with updated fields
   * @return updated member
   */
  Member update(Member updatedMember);

  /**
   * Return all saved members.
   *
   * @return list of members
   */
  List<Member> getAll();

  /**
   * Find member by email.
   *
   * @param email member's email
   * @return Optional with Member object or empty Optional
   */
  Optional<Member> findByEmail(String email);
}
