package com.wcc.platform.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.domain.platform.SocialNetworkType;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.type.MemberType;
import com.wcc.platform.repository.MembersRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class PlatformServiceMemberIntegrationTest {

  public static final String MEMBER_EMAIL = "member@wcc.com";
  @Autowired private PlatformService service;
  @Autowired private MembersRepository repository;

  @BeforeEach
  void setUp() {
    repository.deleteByEmail(MEMBER_EMAIL);
  }

  @Test
  void testSaveMember() {
    var member = createMemberTest(MemberType.MEMBER);
    var result = service.createMember(member);

    assertEquals(member, result);
  }

  @Test
  void testGetAll() {
    var member = createMemberTest(MemberType.VOLUNTEER);
    service.createMember(member);

    var result = service.getAllMembers();

    assertEquals(1, result.size());
  }

  private Member createMemberTest(final MemberType type) {
    return Member.builder()
        .fullName("fullName " + type.name())
        .position("position " + type.name())
        .email(MEMBER_EMAIL)
        .slackDisplayName("Slack name")
        .country(new Country("GB", "United Kingdom"))
        .memberTypes(List.of(type))
        .images(List.of())
        .network(List.of(new SocialNetwork(SocialNetworkType.EMAIL, MEMBER_EMAIL)))
        .build();
  }
}
