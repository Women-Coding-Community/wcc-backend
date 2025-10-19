package com.wcc.platform.repository.postgres;

import static com.wcc.platform.domain.platform.SocialNetworkType.SLACK;
import static org.assertj.core.api.Assertions.assertThat;
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

  private static final String MAIL = "dev_member@email.com";

  @Autowired private PostgresMemberRepository repository;

  private Member member;

  @BeforeEach
  void setUp() {
    member =
        Member.builder()
            .fullName("Full name user")
            .position("Developer")
            .email(MAIL)
            .slackDisplayName("slack_name")
            .country(new Country("ES", "Spain"))
            .city("Valencia")
            .companyName("CompanyName")
            .memberTypes(List.of(MemberType.LEADER))
            .images(List.of(new Image("image.png", "alt image", ImageType.DESKTOP)))
            .network(List.of(new SocialNetwork(SLACK, "slack_link")))
            .build();

    repository.deleteByEmail(member.getEmail());
  }

  @Test
  void testCreateAndUpdate() {
    var newMember = repository.create(member);
    newMember.setImages(List.of());

    var member2 =
        Member.builder()
            .id(newMember.getId())
            .fullName("Full name user 2")
            .position("Developer 2")
            .email(MAIL)
            .slackDisplayName("slack_name 2")
            .country(new Country("PT", "Portugal"))
            .city("Lisbon")
            .images(List.of(new Image("image2.png", "alt image2", ImageType.DESKTOP)))
            .companyName("CompanyName2")
            .memberTypes(List.of(MemberType.LEADER))
            .network(List.of(new SocialNetwork(SLACK, "slack_link_2")))
            .build();

    var updatedMember = repository.update(newMember.getId(), member2);
    assertEquals(member2, updatedMember, "Should update member attributes");
    assertThat(repository.getAll()).isNotEmpty();
    assertTrue(repository.findByEmail(MAIL).isPresent());
  }

  @Test
  void createReturnEmptyForNotFoundMemberId() {
    var optionalMember = repository.findById(7L);

    assertTrue(optionalMember.isEmpty(), "Should not find optionalMember with this id");
  }

  @Test
  void findByEmailNotFound() {
    var optionalMember = repository.findByEmail("notFoundEmail@wcc.com");

    assertTrue(optionalMember.isEmpty(), "Should not find optionalMember with this email");
  }
}
