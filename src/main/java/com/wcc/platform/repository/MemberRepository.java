package com.wcc.platform.repository;

import com.wcc.platform.domain.platform.Member;
import java.util.List;

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
   * Return all saved members.
   *
   * @return list of members
   */
  List<Member> getAll();
}
