package com.wcc.platform.repository;

import com.wcc.platform.domain.platform.Member;
import java.util.List;

public interface MemberRepository {

    void save(Member member);

    List<Member> getAll();
}
