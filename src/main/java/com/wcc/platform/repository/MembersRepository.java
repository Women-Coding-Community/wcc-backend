package com.wcc.platform.repository;

import com.wcc.platform.domain.platform.Member;
import java.util.Optional;

public interface MembersRepository extends CrudRepository<Member, Long> {
  Optional<Member> findByEmail(String email);
}
