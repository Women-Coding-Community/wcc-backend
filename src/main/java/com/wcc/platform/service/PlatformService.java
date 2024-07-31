package com.wcc.platform.service;

import com.wcc.platform.domain.platform.Member;
import com.wcc.platform.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Platform service. */
@Service
public class PlatformService {

  private final MemberRepository memberRepository;

  @Autowired
  public PlatformService(final MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
  }

  /** Write Pojo Member to JSON. */
  public Member createMember(final Member member) {
    return memberRepository.save(member);
  }
}
