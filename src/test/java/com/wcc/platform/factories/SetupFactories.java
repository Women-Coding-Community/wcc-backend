package com.wcc.platform.factories;

import static com.wcc.platform.factories.SetUpStyleFactories.backgroundSecondary;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.configuration.ObjectMapperConfig;
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
import com.wcc.platform.domain.cms.pages.programme.ProgrammeItem;
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
import java.util.List;

/** Setup Factory tests. */
public class SetupFactories {

  public static final int DEFAULT_CURRENT_PAGE = 1;
  public static final int DEFAULT_PAGE_SIZE = 10;
  public static final String HERO_TITLE = "Hero title";
  public static final ObjectMapper OBJECT_MAPPER = new ObjectMapperConfig().objectMapper();
  private static final Image IMAGE_TEST = createImageTest(ImageType.MOBILE);

  /** Factory test. */
  public static Contact createContactTest() {
    return new Contact(
        "Contact Us",
        "Contact description",
        List.of(new SocialNetwork(SocialNetworkType.EMAIL, "test@test.com")));
  }

  /** Factory test. */
  public static MemberByType createMemberByTypeTest() {
    final var directors = List.of(createLeadershipMemberTest(MemberType.DIRECTOR));
    final var leaders = List.of(createLeadershipMemberTest(MemberType.LEADER));
    final var evangelist = List.of(createLeadershipMemberTest(MemberType.EVANGELIST));
    return new MemberByType(directors, leaders, evangelist);
  }

  /** Factory test for CommonSection. */
  public static CommonSection createCommonSectionTest(final String title) {
    return CommonSection.builder()
        .title("title " + title)
        .subtitle("subtitle " + title)
        .description("desc " + title)
        .link(createLinkTest())
        .images(List.of(IMAGE_TEST))
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

  /**
   * Factory test for Partner object.
   *
   * @return Partner object.
   */
  public static Partner createPartnerTest() {
    return new Partner(
        createImageTest(ImageType.DESKTOP), "Partner", "Partner description", createLinkTest());
  }

  private static Image createImageTest(final ImageType type) {
    return new Image(type + ".png", "alt image" + type, type);
  }

  public static LabelLink createLinkTest() {
    return new LabelLink("link_title", "link_label", "link_uri");
  }

  /** Factory test for the hero section. */
  public static HeroSection createHeroSectionTest() {
    return new HeroSection(
        HERO_TITLE,
        "Hero description",
        List.of(createImageTest(ImageType.DESKTOP)),
        backgroundSecondary());
  }

  /** Factory test for the hero section. */
  public static HeroSection createNoImageHeroSectionTest() {
    return new HeroSection(HERO_TITLE, "Hero description", null, null);
  }
}
