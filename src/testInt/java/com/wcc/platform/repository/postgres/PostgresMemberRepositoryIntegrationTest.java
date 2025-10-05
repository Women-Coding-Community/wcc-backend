package com.wcc.platform.repository.postgres;

import static com.wcc.platform.domain.platform.SocialNetworkType.SLACK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.attributes.ImageType;
import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.type.MemberType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PostgresMemberRepositoryIntegrationTest extends DefaultDatabaseSetup {

  private static final long MEMBER_ID = 1L;
  private static final long MEMBER_ID_2 = 2L;

  @Autowired private PostgresMemberRepository repository;

  private Member member;
  private Member member2;

  @BeforeEach
  void setUp() {
    member =
        Member.builder()
            .id(MEMBER_ID)
            .fullName("Full name user")
            .position("Developer")
            .email("dev@email.com")
            .slackDisplayName("slack_name")
            .country(new Country("ES", "Spain"))
            .city("Valencia")
            .companyName("CompanyName")
            .memberTypes(List.of(MemberType.LEADER))
            .images(List.of(new Image("image.png", "alt image", ImageType.DESKTOP)))
            .network(List.of(new SocialNetwork(SLACK, "slack_link")))
            .build();

    member2 =
        Member.builder()
            .id(MEMBER_ID_2)
            .fullName("Full name user 2")
            .position("Developer 2")
            .email("dev@email.com 2")
            .slackDisplayName("slack_name 2")
            .country(new Country("PT", "Portugal"))
            .city("Lisbon")
            .images(List.of(new Image("image2.png", "alt image2", ImageType.DESKTOP)))
            .companyName("CompanyName2")
            .memberTypes(List.of(MemberType.LEADER))
            .network(List.of(new SocialNetwork(SLACK, "slack_link_2")))
            .build();

    repository.deleteById(MEMBER_ID_2);
    repository.deleteById(MEMBER_ID);
  }

  @Test
  void testCreateAndUpdate() {
    repository.create(member);
    member2.setId(MEMBER_ID);
    member2.setImages(List.of());

    var updatedMember = repository.update(MEMBER_ID, member2);
    assertEquals(member2, updatedMember, "Should update member attributes");

    var members = repository.getAll();
    assertEquals(1, members.size(), "Should have only one member");

    var member = repository.findByEmail(member2.getEmail());
    assertTrue(member.isPresent());
  }

  @Test
  void createReturnEmptyForNotFoundMemberId() {
    var member = repository.findById(7L);

    assertTrue(member.isEmpty(), "Should not find member with this id");
  }

  @Test
  void findByEmailNotFound() {
    var member = repository.findByEmail("notFoundEmail@wcc.com");

    assertTrue(member.isEmpty(), "Should not find member with this email");
  }
}
