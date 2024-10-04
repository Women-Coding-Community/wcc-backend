package com.wcc.platform.integrationtests;

import static com.wcc.platform.factories.SetupFactories.createMemberTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.wcc.platform.domain.platform.MemberType;
import com.wcc.platform.service.PlatformService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class PlatformServiceIntegrationTest extends SurrealDbIntegrationTest {

  @Autowired private PlatformService service;

  @Test
  void testSaveMember() {
    var member = createMemberTest(MemberType.MEMBER);
    var result = service.createMember(member);

    assertEquals(member, result);
  }

  @Test
  void testGetAll() {

    var total = service.getAll().size();

    var member = createMemberTest(MemberType.MEMBER);
    service.createMember(member);

    var result = service.getAll();

    assertEquals(total + 1, result.size());
  }
}
