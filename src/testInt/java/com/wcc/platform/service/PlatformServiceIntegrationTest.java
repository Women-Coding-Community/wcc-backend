package com.wcc.platform.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.domain.platform.SocialNetworkType;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.type.MemberType;
import java.io.File;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class PlatformServiceIntegrationTest {

  private final File testFile;
  @Autowired private PlatformService service;

  public PlatformServiceIntegrationTest(
      @Value("${file.storage.directory}") final String directoryPath) {
    super();
    String testFileName = "members.json";
    testFile = new File(directoryPath + File.separator + testFileName);
  }

  @Test
  void testSaveMember() {
    var member = createMemberTest(MemberType.MEMBER);
    var result = service.createMember(member);

    assertEquals(member, result);
  }

  @Test
  void testGetAll() {
    var total = service.getAllMembers().size();

    var member = createMemberTest(MemberType.MEMBER);
    service.createMember(member);

    var result = service.getAllMembers();

    assertEquals(total + 1, result.size());
  }

  private Member createMemberTest(final MemberType type) {
    return Member.builder()
        .fullName("fullName " + type.name())
        .position("position " + type.name())
        .email("member@wcc.com")
        .slackDisplayName("Slack name")
        .memberTypes(List.of(type))
        .network(List.of(new SocialNetwork(SocialNetworkType.EMAIL, "member@wcc.com")))
        .build();
  }
}
