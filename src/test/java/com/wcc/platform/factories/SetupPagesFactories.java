package com.wcc.platform.factories;

import static com.wcc.platform.factories.SetupFactories.DEFAULT_CURRENT_PAGE;
import static com.wcc.platform.factories.SetupFactories.DEFAULT_PAGE_SIZE;
import static com.wcc.platform.factories.SetupFactories.createCommonSectionTest;
import static com.wcc.platform.factories.SetupFactories.createHeroSectionTest;
import static com.wcc.platform.factories.SetupFactories.createListSectionEventTest;
import static com.wcc.platform.factories.SetupFactories.createListSectionPartnerTest;
import static com.wcc.platform.factories.SetupFactories.createListSectionProgrammeItemTest;
import static com.wcc.platform.factories.SetupFactories.createListSectionTest;
import static com.wcc.platform.factories.SetupFactories.createNoImageHeroSectionTest;
import static com.wcc.platform.factories.SetupMentorshipFactories.createFeedbackSectionTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.configuration.ObjectMapperConfig;
import com.wcc.platform.domain.cms.PageType;
import com.wcc.platform.domain.cms.attributes.Contact;
import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.attributes.ImageType;
import com.wcc.platform.domain.cms.attributes.LabelLink;
import com.wcc.platform.domain.cms.pages.CollaboratorPage;
import com.wcc.platform.domain.cms.pages.LandingPage;
import com.wcc.platform.domain.cms.pages.PageMetadata;
import com.wcc.platform.domain.cms.pages.Pagination;
import com.wcc.platform.domain.cms.pages.TeamPage;
import com.wcc.platform.domain.cms.pages.aboutus.AboutUsPage;
import com.wcc.platform.domain.cms.pages.aboutus.CelebrateHerPage;
import com.wcc.platform.domain.cms.pages.aboutus.CodeOfConductPage;
import com.wcc.platform.domain.cms.pages.aboutus.PartnersPage;
import com.wcc.platform.domain.platform.AboutHer;
import com.wcc.platform.domain.platform.Member;
import com.wcc.platform.domain.platform.MemberType;
import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.domain.platform.SocialNetworkType;
import com.wcc.platform.utils.FileUtil;
import java.util.Collections;
import java.util.List;

/** Setup Factory tests. */
public class SetupPagesFactories {

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
        SetupFactories.createMemberByTypeTest());
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
        new PageMetadata(new Pagination(1, 1, DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE)),
        createNoImageHeroSectionTest(),
        createCommonSectionTest(),
        createContactTest(),
        List.of(
            Member.builder()
                .fullName("fullName " + MemberType.MEMBER.name())
                .position("position " + MemberType.MEMBER.name())
                .email("member@wcc.com")
                .slackDisplayName("Slack name")
                .country(new Country("Country code", "Country name"))
                .city("City")
                .companyName("Company name")
                .memberTypes(List.of(MemberType.MEMBER))
                .images(List.of(new Image("image.png", "alt image", ImageType.DESKTOP)))
                .network(
                    List.of(new SocialNetwork(SocialNetworkType.LINKEDIN, "collaborator_link")))
                .build()));
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

    var aboutHer =
        AboutHer.builder()
            .listOfName(List.of("Liliia", "Anna"))
            .description("Description")
            .link(new LabelLink("linkedIn", "linkedIn", "https://linkedIn.com/lilrafil"))
            .build();

    return new CelebrateHerPage(
        pageId,
        createHeroSectionTest(),
        createCommonSectionTest(),
        Collections.singletonList(aboutHer));
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
