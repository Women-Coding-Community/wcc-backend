package com.wcc.platform.factories;

import static com.wcc.platform.domain.platform.SocialNetworkType.TWITTER;
import static com.wcc.platform.domain.platform.SocialNetworkType.YOUTUBE;
import static com.wcc.platform.factories.SetUpStyleFactories.backgroundSecondary;
import static com.wcc.platform.factories.SetupMentorshipFactories.createFeedbackSectionTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.configuration.ObjectMapperConfig;
import com.wcc.platform.domain.cms.PageType;
import com.wcc.platform.domain.cms.attributes.CmsIcon;
import com.wcc.platform.domain.cms.attributes.CommonSection;
import com.wcc.platform.domain.cms.attributes.Contact;
import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.HeroSection;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.attributes.ImageType;
import com.wcc.platform.domain.cms.attributes.LabelLink;
import com.wcc.platform.domain.cms.attributes.ListSection;
import com.wcc.platform.domain.cms.attributes.MemberByType;
import com.wcc.platform.domain.cms.pages.CollaboratorPage;
import com.wcc.platform.domain.cms.pages.FooterSection;
import com.wcc.platform.domain.cms.pages.LandingPage;
import com.wcc.platform.domain.cms.pages.PageMetadata;
import com.wcc.platform.domain.cms.pages.Pagination;
import com.wcc.platform.domain.cms.pages.TeamPage;
import com.wcc.platform.domain.cms.pages.aboutus.AboutUsPage;
import com.wcc.platform.domain.cms.pages.aboutus.CelebrateHerPage;
import com.wcc.platform.domain.cms.pages.aboutus.CodeOfConductPage;
import com.wcc.platform.domain.cms.pages.aboutus.PartnersPage;
import com.wcc.platform.domain.cms.pages.programme.ProgrammeItem;
import com.wcc.platform.domain.platform.AboutHer;
import com.wcc.platform.domain.platform.Event;
import com.wcc.platform.domain.platform.LeadershipMember;
import com.wcc.platform.domain.platform.Member;
import com.wcc.platform.domain.platform.MemberDto;
import com.wcc.platform.domain.platform.MemberType;
import com.wcc.platform.domain.platform.Partner;
import com.wcc.platform.domain.platform.ProgramType;
import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.domain.platform.SocialNetworkType;
import com.wcc.platform.utils.FileUtil;
import java.io.File;
import java.util.Collections;
import java.util.List;

/** Setup Factory tests. */
public class SetupFactories {

  public static final int DEFAULT_CURRENT_PAGE = 1;
  public static final int DEFAULT_PAGE_SIZE = 10;
  public static final String HERO_TITLE = "Hero title";

  public static final ObjectMapper OBJECT_MAPPER = new ObjectMapperConfig().objectMapper();

  /** Factory test. */
  public static Contact createContactTest() {
    return new Contact(
        "Contact Us",
        "Contact description",
        List.of(new SocialNetwork(SocialNetworkType.EMAIL, "test@test.com")));
  }

  /**
   * Factory test for TeamPage.
   *
   * @return TeamPage object.
   */
  public static TeamPage createTeamPageTest() {
    final String pageId = PageType.TEAM.getId();
    return new TeamPage(
        pageId,
        createNoImageHeroSectionTest(),
        createCommonSectionTest(),
        createContactTest(),
        createMemberByTypeTest());
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
    final String pageId = PageType.COLLABORATOR.getId();
    return new CollaboratorPage(
        pageId,
        createPaginationTest(),
        createNoImageHeroSectionTest(),
        createCommonSectionTest(),
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

  /**
   * Factory test for PartnersPage.
   *
   * @return PartnersPage object.
   */
  public static PartnersPage createPartnersPageTest() {
    final String pageId = PageType.PARTNERS.getId();

    return new PartnersPage(
        pageId,
        createNoImageHeroSectionTest(),
        createListSectionTest(),
        createContactTest(),
        createListSectionPartnerTest());
  }

  /**
   * Factory test for PartnersPage.
   *
   * @param fileName json resource file.
   * @return PartnersPage object.
   */
  public static PartnersPage createPartnersPageTest(final String fileName) {
    try {
      String content = FileUtil.readFileAsString(fileName);
      return OBJECT_MAPPER.readValue(content, PartnersPage.class);
    } catch (JsonProcessingException e) {
      return createPartnersPageTest();
    }
  }

  /** Code of conduct factory for testing. */
  public static CodeOfConductPage createCodeOfConductPageTest() {
    final String pageId = PageType.CODE_OF_CONDUCT.getId();
    return new CodeOfConductPage(
        pageId,
        createNoImageHeroSectionTest(),
        createCommonSectionTest(),
        List.of(createListSectionTest()));
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

  /** Factory test for CommonSection. */
  public static CommonSection createCommonSectionTest(final String title) {
    return CommonSection.builder()
        .title("title " + title)
        .subtitle("subtitle " + title)
        .description("desc " + title)
        .link(createLinkTest())
        .images(List.of(createImageTest()))
        .build();
  }

  /** Factory test for CommonSection. */
  public static CommonSection createCommonSectionTest() {
    return createCommonSectionTest("defaultPage");
  }

  /** Factory test for ListSection. */
  public static ListSection<String> createListSectionTest() {
    return new ListSection<>("title", "description", null, List.of("item_1", "item_2", "item_3"));
  }

  /** Factory test for ListSection. */
  public static ListSection<String> createListSectionTest(final String title) {
    return new ListSection<>(
        title,
        title + "description",
        createLinkTest(),
        List.of("topic1 " + title, "topic2 " + title));
  }

  /** Factory test for ListSection for Event type. */
  public static ListSection<Event> createListSectionEventTest() {
    return new ListSection<>(
        "Events",
        "Description Event",
        null,
        List.of(
            SetupEventFactories.createEventTest(ProgramType.BOOK_CLUB),
            SetupEventFactories.createEventTest(ProgramType.TECH_TALK),
            SetupEventFactories.createEventTest(ProgramType.WRITING_CLUB),
            SetupEventFactories.createEventTest(ProgramType.MACHINE_LEARNING)));
  }

  /** Factory test for ListSection for ProgrammeItem type. */
  public static ListSection<ProgrammeItem> createListSectionProgrammeItemTest() {
    return new ListSection<>(
        "Programmes ",
        "Description Programme",
        null,
        List.of(
            SetupProgrammeFactories.createProgrammeItemsTest(
                ProgramType.MACHINE_LEARNING, CmsIcon.DIVERSITY),
            SetupProgrammeFactories.createProgrammeItemsTest(ProgramType.BOOK_CLUB, CmsIcon.BOOK),
            SetupProgrammeFactories.createProgrammeItemsTest(ProgramType.TECH_TALK, CmsIcon.WORK),
            SetupProgrammeFactories.createProgrammeItemsTest(
                ProgramType.WRITING_CLUB, CmsIcon.CALENDAR)));
  }

  /** Factory test for ListSection for Partner type. */
  public static ListSection<Partner> createListSectionPartnerTest() {
    return new ListSection<>(
        "Meet our partners",
        "Description of partners",
        null,
        List.of(
            createPartnerTest(), createPartnerTest(), createPartnerTest(), createPartnerTest()));
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

  /**
   * Factory test for Partner object.
   *
   * @return Partner object.
   */
  public static Partner createPartnerTest() {
    return new Partner(
        createImageTest(ImageType.DESKTOP), "Partner", "Partner description", createLinkTest());
  }

  public static Image createImageTest(final ImageType type) {
    return new Image(type + ".png", "alt image" + type, type);
  }

  public static Image createImageTest() {
    return createImageTest(ImageType.MOBILE);
  }

  /** Factory test. */
  public static FooterSection createFooterTest() {
    return new FooterSection(
        PageType.FOOTER.getId(),
        "footer_title",
        "footer_subtitle",
        "footer_description",
        createSocialNetworksTest(),
        createLinkTest());
  }

  /** Factory test. */
  public static FooterSection createFooterTest(final String fileName) {
    try {
      String content = FileUtil.readFileAsString(fileName);
      return OBJECT_MAPPER.readValue(content, FooterSection.class);
    } catch (JsonProcessingException e) {
      return createFooterTest();
    }
  }

  public static List<SocialNetwork> createSocialNetworksTest() {
    return List.of(
        new SocialNetwork(YOUTUBE, "youtube.com"), new SocialNetwork(TWITTER, "twitter.com"));
  }

  public static LabelLink createLinkTest() {
    return new LabelLink("link_title", "link_label", "link_uri");
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
    return new HeroSection(
        HERO_TITLE, "Hero description", List.of(createImageTest()), backgroundSecondary());
  }

  /** Factory test for hero section. */
  public static HeroSection createNoImageHeroSectionTest() {
    return new HeroSection(HERO_TITLE, "Hero description", null, null);
  }

  /** About Us factory for testing. */
  public static AboutUsPage createAboutUsPageTest() {
    final String pageId = PageType.ABOUT_US.getId();
    return new AboutUsPage(
        pageId, createHeroSectionTest(), List.of(createListSectionTest()), createContactTest());
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

  /** CelebrateHer factory for testing. */
  public static CelebrateHerPage createCelebrateHerPageTest() {
    final String pageId = PageType.CELEBRATE_HER.getId();
    return new CelebrateHerPage(
        pageId,
        createHeroSectionTest(),
        createCommonSectionTest(),
        Collections.singletonList(createAboutHer()));
  }

  /** CelebrateHer factory for testing. */
  public static CelebrateHerPage createCelebrateHerPageTest(final String fileName) {
    try {
      final String content = FileUtil.readFileAsString(fileName);
      return OBJECT_MAPPER.readValue(content, CelebrateHerPage.class);
    } catch (JsonProcessingException e) {
      return createCelebrateHerPageTest();
    }
  }

  public static AboutHer createAboutHer() {
    return AboutHer.builder()
        .listOfName(List.of("Liliia", "Anna"))
        .description("Description")
        .link(new LabelLink("linkedIn", "linkedIn", "https://linkedIn.com/lilrafil"))
        .build();
  }

  /** Landing Page factory for testing. */
  public static LandingPage createLandingPageTest() {
    final String pageId = PageType.LANDING_PAGE.getId();
    return new LandingPage(
        pageId,
        createHeroSectionTest(),
        createCommonSectionTest("Full Banner Section"),
        createListSectionProgrammeItemTest(),
        createListSectionEventTest(),
        createListSectionEventTest(),
        createFeedbackSectionTest(),
        createCommonSectionTest("Volunteer Section"),
        createListSectionPartnerTest());
  }

  /**
   * Landing Page factory for testing.
   *
   * @param fileName landingPage.json resource file.
   */
  public static LandingPage createLandingPageTest(final String fileName) {
    try {
      final String content = FileUtil.readFileAsString(fileName);
      return OBJECT_MAPPER.readValue(content, LandingPage.class);
    } catch (JsonProcessingException e) {
      return createLandingPageTest();
    }
  }
}
