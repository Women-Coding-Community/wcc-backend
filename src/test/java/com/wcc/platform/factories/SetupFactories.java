package com.wcc.platform.factories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.configuration.ObjectMapperConfig;
import com.wcc.platform.domain.cms.PageType;
import com.wcc.platform.domain.cms.attributes.Contact;
import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.HeroSection;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.attributes.ImageType;
import com.wcc.platform.domain.cms.attributes.LabelLink;
import com.wcc.platform.domain.cms.attributes.MemberByType;
import com.wcc.platform.domain.cms.attributes.Network;
import com.wcc.platform.domain.cms.attributes.PageSection;
import com.wcc.platform.domain.cms.pages.AboutUsPage;
import com.wcc.platform.domain.cms.pages.CodeOfConductPage;
import com.wcc.platform.domain.cms.pages.CollaboratorPage;
import com.wcc.platform.domain.cms.pages.FooterPage;
import com.wcc.platform.domain.cms.pages.Page;
import com.wcc.platform.domain.cms.pages.PageMetadata;
import com.wcc.platform.domain.cms.pages.Pagination;
import com.wcc.platform.domain.cms.pages.Section;
import com.wcc.platform.domain.cms.pages.TeamPage;
import com.wcc.platform.domain.platform.LeadershipMember;
import com.wcc.platform.domain.platform.Member;
import com.wcc.platform.domain.platform.MemberDto;
import com.wcc.platform.domain.platform.MemberType;
import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.domain.platform.SocialNetworkType;
import com.wcc.platform.utils.FileUtil;
import java.io.File;
import java.util.List;

/** Setup Factory tests. */
public class SetupFactories {

  public static final int DEFAULT_CURRENT_PAGE = 1;
  public static final int DEFAULT_PAGE_SIZE = 10;

  public static final ObjectMapper OBJECT_MAPPER = new ObjectMapperConfig().objectMapper();

  /** Factory test. */
  public static Contact createContactTest() {
    return new Contact(
        "Contact Us",
        "Contact description",
        List.of(new SocialNetwork(SocialNetworkType.EMAIL, "test@test.com")));
  }

  public static TeamPage createTeamPageTest() {
    final String pageId = PageType.TEAM.getPageId();
    return new TeamPage(pageId, createPageTest(), createContactTest(), createMemberByTypeTest());
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
        createPaginationTest(),
        createPageTest(),
        createContactTest(),
        List.of(createCollaboratorsTest()));
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

  private static PageMetadata createPaginationTest() {
    return new PageMetadata(new Pagination(1, 1, DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE));
  }

  /** Factory test for page. */
  public static Page createPageTest(final String title) {
    return Page.builder()
        .title("title " + title)
        .subtitle("subtitle " + title)
        .description("desc " + title)
        .link(createLinkTest())
        .images(List.of(createImageTest()))
        .build();
  }

  public static Page createPageTest() {
    return createPageTest("defaultPage");
  }

  public static Section<String> createSectionTest() {
    return new Section<>("title", "description", null, List.of("item_1", "item_2", "item_3"));
  }

  /** Factory test. */
  public static Member createMemberTest(final MemberType type) {
    return Member.builder()
        .fullName("fullName " + type.name())
        .position("position " + type.name())
        .email("member@wcc.com")
        .slackDisplayName("Slack name")
        .country(new Country("Country code", "Country name"))
        .city("City")
        .companyName("Company name")
        .memberTypes(List.of(type))
        .images(List.of(new Image("image.png", "alt image", ImageType.DESKTOP)))
        .network(List.of(new SocialNetwork(SocialNetworkType.LINKEDIN, "collaborator_link")))
        .build();
  }

  /** Factory test to create MemberDto object. */
  public static MemberDto createMemberDtoTest(final MemberType type) {
    return new MemberDto(
        "fullName updated " + type.name(),
        "position updated " + type.name(),
        "Slack name updated",
        new Country("Country code updated", "Country name updated"),
        "City updated",
        "Company name updated",
        List.of(new SocialNetwork(SocialNetworkType.GITHUB, "collaborator_link_updated")),
        List.of(new Image("image_updated.png", "alt image updated", ImageType.MOBILE)));
  }

  /** Factory test to create new member from combining data from member and memberDto. */
  public static Member createUpdatedMemberTest(final Member member, final MemberDto memberDto) {
    return Member.builder()
        .fullName(memberDto.fullName())
        .position(memberDto.position())
        .email(member.getEmail())
        .slackDisplayName(memberDto.slackDisplayName())
        .country(memberDto.country())
        .city(memberDto.city())
        .companyName(memberDto.companyName())
        .memberTypes(member.getMemberTypes())
        .images(memberDto.images())
        .network(memberDto.network())
        .build();
  }

  /** Factory test to get a list of members for testing get members API. */
  public static List<Member> createMembersTest(final String fileName) {
    try {
      final String content = FileUtil.readFileAsString(fileName);
      return OBJECT_MAPPER.readValue(content, new TypeReference<>() {});
    } catch (JsonProcessingException e) {
      return List.of(createMemberTest(MemberType.MEMBER));
    }
  }

  /** Factory test. */
  public static LeadershipMember createLeadershipMemberTest(final MemberType type) {
    return LeadershipMember.leadershipMemberBuilder()
        .fullName("fullName " + type.name())
        .position("position " + type.name())
        .email("member@wcc.com")
        .slackDisplayName("Slack name")
        .country(new Country("Country code", "Country name"))
        .city("City")
        .companyName("Company name")
        .memberTypes(List.of(type))
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
        .slackDisplayName("Slack name")
        .country(new Country("Country code", "Country name"))
        .city("City")
        .companyName("Company name")
        .memberTypes(List.of(type))
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

  /** Factory test. */
  public static FooterPage createFooterPageTest() {
    return new FooterPage(
        PageType.FOOTER.getPageId(),
        "footer_title",
        "footer_subtitle",
        "footer_description",
        createNetworksTest(),
        createLinkTest());
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

  public static LabelLink createLinkTest() {
    return new LabelLink("link_title", "link_label", "link_uri");
  }

  /** Factory test for page section. */
  public static PageSection createPageSectionTest(final String title) {
    return new PageSection(
        title,
        title + "description",
        createLinkTest(),
        List.of("topic1 " + title, "topic2 " + title));
  }

  /**
   * Factory test for repository file section to delete the content of the file used for testing.
   */
  public static void deleteFile(final File file) {
    if (file.exists()) {
      file.delete();
    }
  }

  /** Factory test for hero section. */
  public static HeroSection createHeroSectionTest() {
    return new HeroSection("title", "hero description", createImageTest());
  }

  /** About Us factory for testing. */
  public static AboutUsPage createAboutUsPageTest() {
    return new AboutUsPage(createPageTest(), List.of(createSectionTest()), createContactTest());
  }

  /** About Us factory for testing. */
  public static AboutUsPage createAboutUsPageTest(final String fileName) {
    try {
      final String content = FileUtil.readFileAsString(fileName);
      return OBJECT_MAPPER.readValue(content, AboutUsPage.class);
    } catch (JsonProcessingException e) {
      return createAboutUsPageTest();
    }
  }
}
