package com.wcc.platform.factories;

import static com.wcc.platform.domain.cms.PageType.AD_HOC_TIMELINE;
import static com.wcc.platform.factories.SetUpStyleFactories.createCustomStyleTest;
import static com.wcc.platform.factories.SetupMentorFactories.createMentorTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wcc.platform.domain.cms.PageType;
import com.wcc.platform.domain.cms.attributes.CommonSection;
import com.wcc.platform.domain.cms.attributes.FaqItem;
import com.wcc.platform.domain.cms.attributes.LabelLink;
import com.wcc.platform.domain.cms.attributes.ListSection;
import com.wcc.platform.domain.cms.pages.mentorship.FeedbackItem;
import com.wcc.platform.domain.cms.pages.mentorship.FeedbackSection;
import com.wcc.platform.domain.cms.pages.mentorship.LongTermTimeLinePage;
import com.wcc.platform.domain.cms.pages.mentorship.MentorFilterSection;
import com.wcc.platform.domain.cms.pages.mentorship.MentorsPage;
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipAdHocTimelinePage;
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipCodeOfConductPage;
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipFaqPage;
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipPage;
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipResourcesPage;
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipStudyGroupsPage;
import com.wcc.platform.domain.cms.pages.mentorship.StudyGroup;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import com.wcc.platform.domain.platform.type.MemberType;
import com.wcc.platform.utils.FileUtil;
import java.time.Year;
import java.util.List;

/** Mentorship test factories. */
public class SetupMentorshipPagesFactories {

  /** Test factory. */
  public static MentorshipPage createMentorshipPageTest(final String fileName) {
    try {
      final String content = FileUtil.readFileAsString(fileName);
      return SetupFactories.OBJECT_MAPPER.readValue(content, MentorshipPage.class);
    } catch (JsonProcessingException e) {
      return createMentorshipPageTest();
    }
  }

  /** Test factory. */
  public static MentorshipPage createMentorshipPageTest() {
    final String pageId = PageType.MENTORSHIP.getId();
    return new MentorshipPage(
        pageId,
        SetupFactories.createNoImageHeroSectionTest(),
        SetupFactories.createCommonSectionTest(),
        SetupFactories.createListSectionTest("Mentor"),
        SetupFactories.createListSectionTest("Mentee"),
        createFeedbackSectionTest());
  }

  /** Test factory. */
  public static MentorsPage createMentorPageTest() {
    final String pageId = PageType.MENTORS.getId();
    var filters =
        MentorFilterSection.builder()
            .types(List.of(MentorshipType.LONG_TERM, MentorshipType.AD_HOC))
            .build();
    return new MentorsPage(
        pageId,
        SetupFactories.createNoImageHeroSectionTest(),
        null,
        filters,
        List.of(createMentorTest().toDto()));
  }

  /** Test factory. */
  public static MentorsPage createMentorsPageTest(final String fileName) {
    try {
      final String content = FileUtil.readFileAsString(fileName);
      return SetupFactories.OBJECT_MAPPER.readValue(content, MentorsPage.class);
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
      return SetupFactories.OBJECT_MAPPER.readValue(content, MentorshipFaqPage.class);
    } catch (JsonProcessingException e) {
      return createMentorshipFaqPageTest();
    }
  }

  public static MentorshipFaqPage createMentorshipFaqPageTest() {
    final String pageId = PageType.MENTORSHIP_FAQ.getId();
    return new MentorshipFaqPage(
        pageId,
        SetupFactories.createNoImageHeroSectionTest(),
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
      return SetupFactories.OBJECT_MAPPER.readValue(content, MentorshipCodeOfConductPage.class);
    } catch (JsonProcessingException e) {
      return createMentorshipConductPageTest();
    }
  }

  public static MentorshipCodeOfConductPage createMentorshipConductPageTest() {
    final String pageId = PageType.MENTORSHIP_CONDUCT.getId();
    return new MentorshipCodeOfConductPage(
        pageId,
        SetupFactories.createNoImageHeroSectionTest(),
        SetupFactories.createListSectionTest("Mentee Code of Conduct"),
        SetupFactories.createListSectionTest("Mentor Code of Conduct"),
        createCommonSectionOnlyLinkTest());
  }

  /** Test factory for LongTerm Timeline Page. * */
  public static LongTermTimeLinePage createLongTermTimeLinePageTest(final String fileName) {
    try {
      final String content = FileUtil.readFileAsString(fileName);
      return SetupFactories.OBJECT_MAPPER.readValue(content, LongTermTimeLinePage.class);
    } catch (JsonProcessingException e) {
      return createLongTermTimeLinePageTest();
    }
  }

  public static LongTermTimeLinePage createLongTermTimeLinePageTest() {
    final String pageId = PageType.MENTORSHIP_LONG_TIMELINE.getId();
    return new LongTermTimeLinePage(
        pageId,
        SetupFactories.createNoImageHeroSectionTest(),
        new ListSection<>("Timeline Events", "description", null, List.of()));
  }

  private static CommonSection createCommonSectionOnlyLinkTest() {
    return new CommonSection(null, null, null, SetupFactories.createLinkTest(), null, null);
  }

  /** Test factory for Study Group Page. */
  public static MentorshipStudyGroupsPage createMentorshipStudyGroupPageTest(
      final String fileName) {
    try {
      final String content = FileUtil.readFileAsString(fileName);
      return SetupFactories.OBJECT_MAPPER.readValue(content, MentorshipStudyGroupsPage.class);
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
        SetupFactories.createNoImageHeroSectionTest(),
        SetupFactories.createCommonSectionTest(),
        SetupFactories.createContactTest(),
        new ListSection<>("Study Groups", null, null, List.of(studyGroup)),
        createCustomStyleTest());
  }

  /** Test factory for Ad Hoc Timeline Page. */
  public static MentorshipAdHocTimelinePage createMentorshipAdHocTimelinePageTest() {
    final String pageId = AD_HOC_TIMELINE.getId();

    return new MentorshipAdHocTimelinePage(
        pageId,
        SetupFactories.createNoImageHeroSectionTest(),
        new ListSection<>("Events", "description", null, null));
  }

  /** Test factory for Mentorship Resources Page with file. */
  public static MentorshipResourcesPage createMentorshipResourcesPageTest(final String fileName) {
    try {
      final String content = FileUtil.readFileAsString(fileName);
      return SetupFactories.OBJECT_MAPPER.readValue(content, MentorshipResourcesPage.class);
    } catch (JsonProcessingException e) {
      return createMentorshipResourcesPageTest();
    }
  }

  /** Test factory for Mentorship Resources Page with file. */
  public static MentorshipResourcesPage createMentorshipResourcesPageTest() {
    try {
      final String content = FileUtil.readFileAsString(PageType.MENTORSHIP_RESOURCES.getFileName());
      return SetupFactories.OBJECT_MAPPER.readValue(content, MentorshipResourcesPage.class);
    } catch (JsonProcessingException e) {
      return createMentorshipResourcesPageTest();
    }
  }
}
