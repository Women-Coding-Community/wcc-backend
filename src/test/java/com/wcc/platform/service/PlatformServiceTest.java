package com.wcc.platform.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.platform.MemberType;
import com.wcc.platform.factories.SetupFactories;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.ResourceContentRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PlatformServiceTest {

  private MemberRepository memberRepository;
  private ResourceContentRepository resource;
  private PlatformService service;

  @BeforeEach
  void setUp() {
    memberRepository = Mockito.mock(MemberRepository.class);
    resource = Mockito.mock(ResourceContentRepository.class);
    service = new PlatformService(resource, memberRepository);
  }

  @Test
  void whenGetAllGivenValidJson() {
    var member = SetupFactories.createMemberTest(MemberType.MEMBER);
    when(memberRepository.getAll()).thenReturn(List.of(member));
    var response = service.getAll();
    assertEquals(List.of(member), response);
  }
}
