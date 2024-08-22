package com.wcc.platform.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.platform.MemberType;
import com.wcc.platform.factories.SetupFactories;
import com.wcc.platform.repository.MemberRepository;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PlatformServiceTest {
  private MemberRepository memberRepository;
  private PlatformService service;

  @BeforeEach
  void setUp() {
    memberRepository = Mockito.mock(MemberRepository.class);
    service = new PlatformService(memberRepository);
  }

  @Test
  void whenGetAllGivenValidJson() throws IOException {
    var member = SetupFactories.createMemberTest(MemberType.MEMBER);
    when(memberRepository.getAll()).thenReturn(List.of(member));
    var response = service.getAll();
    assertEquals(List.of(member), response);
  }
}
