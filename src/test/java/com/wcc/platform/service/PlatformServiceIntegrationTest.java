package com.wcc.platform.service;

import static com.wcc.platform.factories.SetupFactories.createMemberTest;
import static org.junit.jupiter.api.Assertions.*;

import com.wcc.platform.domain.platform.MemberType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class PlatformServiceIntegrationTest {
  @Autowired private PlatformService service;

  @Test
  void testSaveMember() {
    var member = createMemberTest(MemberType.MEMBER);
    var result = service.createMember(member);

    assertEquals(member, result);
  }

  @Test
  void testGetALl() {
    var total = service.getAll().size();

    var member = createMemberTest(MemberType.MEMBER);
    service.createMember(member);

    var result = service.getAll();

    assertEquals(total + 1, result.size());
  }
}
