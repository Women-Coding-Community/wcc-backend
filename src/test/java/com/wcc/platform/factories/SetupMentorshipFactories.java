package com.wcc.platform.factories;

import static com.wcc.platform.factories.SetUpStyleFactories.createCustomStyleTest;
import static com.wcc.platform.factories.SetupFactories.OBJECT_MAPPER;
import static com.wcc.platform.factories.SetupFactories.createCommonSectionTest;
import static com.wcc.platform.factories.SetupFactories.createContactTest;
import static com.wcc.platform.factories.SetupFactories.createLinkTest;
import static com.wcc.platform.factories.SetupFactories.createListSectionTest;
import static com.wcc.platform.factories.SetupFactories.createMemberTest;
import static com.wcc.platform.factories.SetupFactories.createNoImageHeroSectionTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wcc.platform.domain.cms.PageType;
import com.wcc.platform.domain.cms.attributes.CommonSection;
import com.wcc.platform.domain.cms.attributes.Experience;
import com.wcc.platform.domain.cms.attributes.FaqItem;
import com.wcc.platform.domain.cms.attributes.LabelLink;
import com.wcc.platform.domain.cms.attributes.Languages;
import com.wcc.platform.domain.cms.attributes.ListSection;
import com.wcc.platform.domain.cms.attributes.TechnicalArea;
import com.wcc.platform.domain.cms.pages.mentorship.Availability;
import com.wcc.platform.domain.cms.pages.mentorship.FeedbackItem;
import com.wcc.platform.domain.cms.pages.mentorship.FeedbackSection;
import com.wcc.platform.domain.cms.pages.mentorship.MenteeSection;
import com.wcc.platform.domain.cms.pages.mentorship.MentorsPage;
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipCodeOfConductPage;
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipFaqPage;
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipPage;
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipStudyGroupsPage;
import com.wcc.platform.domain.platform.Member;
import com.wcc.platform.domain.platform.MemberType;
import com.wcc.platform.domain.platform.Mentor;
import com.wcc.platform.domain.platform.MentorshipType;
import com.wcc.platform.domain.platform.ProfileStatus;
import com.wcc.platform.domain.platform.ResourceContent;
import com.wcc.platform.domain.platform.Skills;
import com.wcc.platform.domain.platform.StudyGroup;
import com.wcc.platform.domain.platform.type.ResourceType;
import com.wcc.platform.utils.FileUtil;
import java.time.Month;
import java.time.Year;
import java.util.List;

/** Mentorship test factories. */
public class SetupMentorshipFactories {

  /** Test factory. */
  public static MentorshipPage createMentorshipPageTest(final String fileName) {
    try {
      final String content = FileUtil.readFileAsString(fileName);
      return OBJECT_MAPPER.readValue(content, MentorshipPage.class);
    } catch (JsonProcessingException e) {
      return createMentorshipPageTest();
    }
  }

  /** Test factory. */
  public static MentorshipPage createMentorshipPageTest() {
    final String pageId = PageType.MENTORSHIP.getId();
    return new MentorshipPage(
        pageId,
        createNoImageHeroSectionTest(),
        createCommonSectionTest(),
        createListSectionTest("Mentor"),
        createListSectionTest("Mentee"),
        createFeedbackSectionTest());
  }

  /** Test factory. */
  public static MentorsPage createMentorPageTest() {
    final String pageId = PageType.MENTORS.getId();
    return new MentorsPage(pageId, createNoImageHeroSectionTest(), List.of(createMentorTest()));
  }

  /** Test factory. */
  public static MentorsPage createMentorsPageTest(final String fileName) {
    try {
      final String content = FileUtil.readFileAsString(fileName);
      return OBJECT_MAPPER.readValue(content, MentorsPage.class);
    } catch (JsonProcessingException e) {
      return createMentorPageTest();
    }
  }

  public static FeedbackItem createFeedbackItemTest(final MemberType memberType) {
    return new FeedbackItem(
        "Person Name", "Nice feedback", memberType, Year.of(2023), null, null, null);
  }

  /**
   * Factory test for FeedbackSection class.
   *
   * @return FeedbackSection
   */
  public static FeedbackSection createFeedbackSectionTest() {
    return new FeedbackSection(
        "Feedback1",
        List.of(
            createFeedbackItemTest(MemberType.MENTOR), createFeedbackItemTest(MemberType.MENTEE)));
  }

  public static MentorshipFaqPage createMentorshipFaqPageTest(final String fileName) {
    try {
      final String content = FileUtil.readFileAsString(fileName);
      return OBJECT_MAPPER.readValue(content, MentorshipFaqPage.class);
    } catch (JsonProcessingException e) {
      return createMentorshipFaqPageTest();
    }
  }

  public static MentorshipFaqPage createMentorshipFaqPageTest() {
    final String pageId = PageType.MENTORSHIP_FAQ.getId();
    return new MentorshipFaqPage(
        pageId,
        createNoImageHeroSectionTest(),
        createListFaqSectionTest(new FaqItem("Common", "Common FAQ")),
        createListFaqSectionTest(new FaqItem("Mentor", "Mentor FAQ")),
        createListFaqSectionTest(new FaqItem("Mentee", "Mentee FAQ")));
  }

  private static ListSection<FaqItem> createListFaqSectionTest(final FaqItem faqItem) {
    return new ListSection<>("FAQ", null, null, List.of(faqItem));
  }

  public static MentorshipCodeOfConductPage createMentorshipConductPageTest(final String fileName) {
    try {
      final String content = FileUtil.readFileAsString(fileName);
      return OBJECT_MAPPER.readValue(content, MentorshipCodeOfConductPage.class);
    } catch (JsonProcessingException e) {
      return createMentorshipConductPageTest();
    }
  }

  public static MentorshipCodeOfConductPage createMentorshipConductPageTest() {
    final String pageId = PageType.MENTORSHIP_CONDUCT.getId();
    return new MentorshipCodeOfConductPage(
        pageId,
        createNoImageHeroSectionTest(),
        createListSectionTest("Mentee Code of Conduct"),
        createListSectionTest("Mentor Code of Conduct"),
        createCommonSectionOnlyLinkTest());
  }

  private static CommonSection createCommonSectionOnlyLinkTest() {
    return new CommonSection(null, null, null, createLinkTest(), null, null);
  }

  /** Test factory for Study Group Page. */
  public static MentorshipStudyGroupsPage createMentorshipStudyGroupPageTest(
      final String fileName) {
    try {
      final String content = FileUtil.readFileAsString(fileName);
      return OBJECT_MAPPER.readValue(content, MentorshipStudyGroupsPage.class);
    } catch (JsonProcessingException e) {
      return createMentorshipStudyGroupPageTest();
    }
  }

  /** Test factory for StudyGroupPage. */
  public static MentorshipStudyGroupsPage createMentorshipStudyGroupPageTest() {
    final String pageId = PageType.STUDY_GROUPS.getId();
    StudyGroup studyGroup =
        new StudyGroup(
            "group-1",
            "Study Group 1 description",
            "Coordinator Name",
            new LabelLink("test link", "test label", "http://link-1"),
            3);

    return new MentorshipStudyGroupsPage(
        pageId,
        createNoImageHeroSectionTest(),
        createCommonSectionTest(),
        createContactTest(),
        new ListSection<>("Study Groups", null, null, List.of(studyGroup)),
        createCustomStyleTest());
  }

  /** Mentor Builder. */
  public static Mentor createMentorTest() {
    final Member member = createMemberTest(MemberType.MENTOR);
    return Mentor.mentorBuilder()
        .fullName(member.getFullName())
        .position(member.getPosition())
        .email(member.getEmail())
        .slackDisplayName(member.getSlackDisplayName())
        .country(member.getCountry())
        .city(member.getCity())
        .companyName(member.getCompanyName())
        .images(member.getImages())
        .network(member.getNetwork())
        .profileStatus(ProfileStatus.ACTIVE)
        .bio("Mentor bio")
        .skills(
            new Skills(
                2,
                Experience.YEARS_1_TO_5,
                List.of(TechnicalArea.BACKEND, TechnicalArea.FRONTEND),
                List.of(Languages.JAVASCRIPT)))
        .menteeSection(
            new MenteeSection(
                List.of(MentorshipType.LONG_TERM),
                new Availability(List.of(Month.APRIL, Month.JUNE), 2),
                "ideal mentee description",
                List.of("focus"),
                "additional"))
        .spokenLanguages(List.of("English", "Spanish"))
        .feedbackSection(createFeedbackSectionTest())
        .resources(
            List.of(
                new ResourceContent(
                    "id",
                    "resource name",
                    "description",
                    "raw content",
                    ResourceType.DOCUMENT,
                    null,
                    null)))
        .build();
  }

  /** Mentor Builder. */
  public static Mentor createMentorTest(final String mentorName) {
    final Member member = createMemberTest(MemberType.MENTOR);
    return Mentor.mentorBuilder()
        .fullName(mentorName)
        .position(member.getPosition())
        .email(member.getEmail())
        .country(member.getCountry())
        .images(member.getImages())
        .profileStatus(ProfileStatus.ACTIVE)
        .bio("Mentor bio")
        .skills(
            new Skills(
                2,
                Experience.YEARS_1_TO_5,
                List.of(TechnicalArea.BACKEND, TechnicalArea.FRONTEND),
                List.of(Languages.JAVASCRIPT)))
        .menteeSection(
            new MenteeSection(
                List.of(MentorshipType.LONG_TERM),
                new Availability(List.of(Month.APRIL, Month.JUNE), 2),
                "ideal mentee description",
                List.of("focus"),
                "additional"))
        .build();
  }
}
