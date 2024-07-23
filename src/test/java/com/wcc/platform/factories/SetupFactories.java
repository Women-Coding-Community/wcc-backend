package com.wcc.platform.factories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.configuration.ObjectMapperConfig;
import com.wcc.platform.domain.cms.attributes.Contact;
import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.attributes.ImageType;
import com.wcc.platform.domain.cms.attributes.LabelLink;
import com.wcc.platform.domain.cms.attributes.MemberByType;
import com.wcc.platform.domain.cms.attributes.Network;
import com.wcc.platform.domain.cms.attributes.PageSection;
import com.wcc.platform.domain.cms.attributes.SimpleLink;
import com.wcc.platform.domain.cms.pages.CodeOfConductPage;
import com.wcc.platform.domain.cms.pages.CollaboratorPage;
import com.wcc.platform.domain.cms.pages.FooterPage;
import com.wcc.platform.domain.cms.pages.Page;
import com.wcc.platform.domain.cms.pages.Section;
import com.wcc.platform.domain.cms.pages.TeamPage;
import com.wcc.platform.domain.platform.LeadershipMember;
import com.wcc.platform.domain.platform.Member;
import com.wcc.platform.domain.platform.MemberType;
import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.domain.platform.SocialNetworkType;
import com.wcc.platform.utils.FileUtil;
import java.util.List;

/** Setup Factory tests. */
public class SetupFactories {

  public static final ObjectMapper OBJECT_MAPPER = new ObjectMapperConfig().objectMapper();

  public static Contact createContactTest() {
    return new Contact(
        "Contact Us", List.of(new SocialNetwork(SocialNetworkType.EMAIL, "test@test.com")));
  }

  public static TeamPage createTeamPageTest() {
    return new TeamPage(createPageTest(), createContactTest(), createMemberByTypeTest());
  }

  /** Factory test. */
  public static TeamPage createTeamPageTest(final String fileName) {
    try {
      final String content = FileUtil.readFileAsString(fileName);
      return OBJECT_MAPPER.readValue(content, TeamPage.class);
    } catch (JsonProcessingException e) {
      return createTeamPageTest();
    }
  }

  /** Factory test. */
  public static CollaboratorPage createCollaboratorPageTest() {
    return new CollaboratorPage(
        createPageTest(), createContactTest(), List.of(createCollaboratorsTest()));
  }

  /** Factory test. */
  public static CollaboratorPage createCollaboratorPageTest(final String fileName) {
    try {
      String content = FileUtil.readFileAsString(fileName);
      return OBJECT_MAPPER.readValue(content, CollaboratorPage.class);
    } catch (JsonProcessingException e) {
      return createCollaboratorPageTest();
    }
  }

  public static CodeOfConductPage createCodeOfConductPageTest() {
    return new CodeOfConductPage(createPageTest(), List.of(createSectionTest()));
  }

  /** Code of conduct factory for testing. */
  public static CodeOfConductPage createCodeOfConductPageTest(final String fileName) {
    try {
      final String content = FileUtil.readFileAsString(fileName);
      return OBJECT_MAPPER.readValue(content, CodeOfConductPage.class);
    } catch (JsonProcessingException e) {
      return createCodeOfConductPageTest();
    }
  }

  /** Factory test. */
  public static MemberByType createMemberByTypeTest() {
    final var directors = List.of(createLeadershipMemberTest(MemberType.DIRECTOR));
    final var leaders = List.of(createLeadershipMemberTest(MemberType.LEADER));
    final var evangelist = List.of(createLeadershipMemberTest(MemberType.EVANGELIST));
    return new MemberByType(directors, leaders, evangelist);
  }

  public static Member createCollaboratorsTest() {
    return createCollaboratorMemberTest(MemberType.MEMBER);
  }

  public static Page createPageTest() {
    return new Page("title", "subtitle", "description");
  }

  public static Section createSectionTest() {
    return new Section("title", "description", List.of("item_1", "item_2", "item_3"));
  }

  /** Factory test. */
  public static Member createMemberTest(final MemberType type) {
    return Member.builder()
        .fullName("fullName " + type.name())
        .position("position " + type.name())
        .email("member@wcc.com")
        .country(new Country("Country code", "Country name"))
        .city("City")
        .jobTitle("Job title")
        .companyName("Company name")
        .memberType(type)
        .images(List.of(new Image("image.png", "alt image", ImageType.DESKTOP)))
        .network(List.of(new SocialNetwork(SocialNetworkType.LINKEDIN, "collaborator_link")))
        .build();
  }

  /** Factory test. */
  public static LeadershipMember createLeadershipMemberTest(final MemberType type) {
    return LeadershipMember.leadershipMemberBuilder()
        .fullName("fullName " + type.name())
        .position("position " + type.name())
        .email("member@wcc.com")
        .country(new Country("Country code", "Country name"))
        .city("City")
        .jobTitle("Job title")
        .companyName("Company name")
        .memberType(type)
        .images(List.of(new Image("image.png", "alt image", ImageType.DESKTOP)))
        .network(List.of(new SocialNetwork(SocialNetworkType.LINKEDIN, "collaborator_link")))
        .build();
  }

  /** Factory test. */
  public static Member createCollaboratorMemberTest(final MemberType type) {
    return Member.builder()
        .fullName("fullName " + type.name())
        .position("position " + type.name())
        .email("member@wcc.com")
        .country(new Country("Country code", "Country name"))
        .city("City")
        .jobTitle("Job title")
        .companyName("Company name")
        .memberType(type)
        .images(List.of(new Image("image.png", "alt image", ImageType.DESKTOP)))
        .network(List.of(new SocialNetwork(SocialNetworkType.LINKEDIN, "collaborator_link")))
        .build();
  }

  public static Image createImageTest(final ImageType type) {
    return new Image(type + ".png", "alt image" + type, type);
  }

  public static Image createImageTest() {
    return createImageTest(ImageType.MOBILE);
  }

  public static SocialNetwork createSocialNetworkTest(final SocialNetworkType type) {
    return new SocialNetwork(type, type + ".com");
  }

  /** Factory test. */
  public static SocialNetwork createSocialNetworkTest() {
    return createSocialNetworkTest(SocialNetworkType.INSTAGRAM);
  }

  /** Factory test. */
  public static FooterPage createFooterPageTest() {
    return new FooterPage(
        "footer_title",
        "footer_subtitle",
        "footer_description",
        createNetworksTest(),
        createLabelLinkTest());
  }

  /** Factory test. */
  public static FooterPage createFooterPageTest(final String fileName) {
    try {
      String content = FileUtil.readFileAsString(fileName);
      return OBJECT_MAPPER.readValue(content, FooterPage.class);
    } catch (JsonProcessingException e) {
      return createFooterPageTest();
    }
  }

  public static List<Network> createNetworksTest() {
    return List.of(new Network("type1", "link1"), new Network("type2", "link2"));
  }

  public static LabelLink createLabelLinkTest() {
    return new LabelLink("link_title", "link_label", "link_uri");
  }

  /** Factory test. */
  public static SimpleLink createSimpleLinkTest() {
    return new SimpleLink("Simple Link", "/simple-link");
  }

  /** Factory test for page section. */
  public static PageSection createPageSectionTest(final String title) {
    return new PageSection(
        title,
        title + "description",
        createSimpleLinkTest(),
        List.of("topic1 " + title, "topic2 " + title));
  }
}
