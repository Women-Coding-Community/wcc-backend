package com.wcc.platform.repository;

import com.wcc.platform.domain.platform.Member;
import java.util.List;

/** Interface to save and retrieve member's data to and from a file repository. */
public interface MemberRepository {

  /**
   * Save member to file
   *
   * @param member member to be saved to file
   * @return member
   */
  Member save(Member member);

  /**
   * Read all members from file
   *
   * @return list of members
   */
  List<Member> getAll();
}
