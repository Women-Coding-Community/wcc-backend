package com.wcc.platform.factories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wcc.platform.domain.cms.attributes.*;
import com.wcc.platform.domain.cms.pages.CodeOfConductPage;
import com.wcc.platform.domain.cms.pages.CollaboratorPage;
import com.wcc.platform.domain.cms.pages.FooterPage;
import com.wcc.platform.domain.cms.pages.Page;
import com.wcc.platform.domain.cms.pages.Section;
import com.wcc.platform.domain.cms.pages.TeamPage;
import com.wcc.platform.domain.platform.*;
import com.wcc.platform.utils.FileUtil;
import java.util.List;

public class TestFactories {

  public static Contact createContactTest() {
    return new Contact(
        "Contact Us", List.of(new SocialNetwork(SocialNetworkType.EMAIL, "test@test.com")));
  }

  public static TeamPage createTeamPageTest() {
    return new TeamPage(createPageTest(), createContactTest(), createMemberByTypeTest());
  }

  public static TeamPage createTeamPageTest(String fileName) {
    try {
      String content = FileUtil.readFileAsString(fileName);
      return ObjectMapperTestFactory.getInstance().readValue(content, TeamPage.class);
    } catch (JsonProcessingException e) {
      return createTeamPageTest();
    }
  }

  public static CollaboratorPage createCollaboratorPageTest() {
    return new CollaboratorPage(
        createPageTest(), createContactTest(), List.of(createCollaboratorsTest()));
  }

  public static CollaboratorPage createCollaboratorPageTest(String fileName) {
    try {
      String content = FileUtil.readFileAsString(fileName);
      return ObjectMapperTestFactory.getInstance().readValue(content, CollaboratorPage.class);
    } catch (JsonProcessingException e) {
      return createCollaboratorPageTest();
    }
  }

  public static CodeOfConductPage createCodeOfConductPageTest() {
    return new CodeOfConductPage(createPageTest(), List.of(createSectionTest()));
  }

  public static CodeOfConductPage createCodeOfConductPageTest(String fileName) {
    try {
      String content = FileUtil.readFileAsString(fileName);
      return ObjectMapperTestFactory.getInstance().readValue(content, CodeOfConductPage.class);
    } catch (JsonProcessingException e) {
      return createCodeOfConductPageTest();
    }
  }

  public static MemberByType createMemberByTypeTest() {
    var directors = List.of(createLeadershipMemberTest(MemberType.DIRECTOR));
    var leaders = List.of(createLeadershipMemberTest(MemberType.LEADER));
    var evangelist = List.of(createLeadershipMemberTest(MemberType.EVANGELIST));
    return new MemberByType(directors, leaders, evangelist);
  }

  public static Member createCollaboratorsTest() {
    return (createCollaboratorMemberTest(MemberType.MEMBER));
  }

  public static Page createPageTest() {
    return new Page("title", "subtitle", "description");
  }

  public static Section createSectionTest() {
    return new Section("title", "description", List.of("item_1", "item_2", "item_3"));
  }

  public static Member createMemberTest(MemberType type) {
    var member =
        Member.builder()
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

    return member;
  }

  public static LeadershipMember createLeadershipMemberTest(MemberType type) {
    //    var team = new LeadershipMember();
    // Member member = createMemberTest(type);
    LeadershipMember team =
        LeadershipMember.leadershipMemberBuilder()
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

    return team;
  }

  public static Member createCollaboratorMemberTest(MemberType type) {
    var member =
        Member.builder()
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

    return member;
  }

  public static Image createImageTest(ImageType type) {
    return new Image(type + ".png", "alt image" + type, type);
  }

  public static Image createImageTest() {
    return createImageTest(ImageType.MOBILE);
  }

  public static SocialNetwork createSocialNetworkTest(SocialNetworkType type) {
    return new SocialNetwork(type, type + ".com");
  }

  public static SocialNetwork createSocialNetworkTest() {
    return createSocialNetworkTest(SocialNetworkType.INSTAGRAM);
  }

  public static FooterPage createFooterPageTest() {
    return new FooterPage(
        "footer_title",
        "footer_subtitle",
        "footer_description",
        createNetworksTest(),
        createLabelLinkTest());
  }

  public static FooterPage createFooterPageTest(String fileName) {
    try {
      String content = FileUtil.readFileAsString(fileName);
      return ObjectMapperTestFactory.getInstance().readValue(content, FooterPage.class);
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

  public static SimpleLink createSimpleLinkTest() {
    return new SimpleLink("Simple Link", "/simple-link");
  }

  public static PageSection createPageSectionTest(String title) {
    return new PageSection(
        title,
        title + "description",
        createSimpleLinkTest(),
        List.of("topic1 " + title, "topic2 " + title));
  }
}
