package com.wcc.platform.factories;

import static com.wcc.platform.factories.SetupFactories.OBJECT_MAPPER;
import static com.wcc.platform.factories.SetupFactories.createNoImageHeroSectionTest;
import static com.wcc.platform.factories.SetupFactories.createPageSectionTest;
import static com.wcc.platform.factories.SetupFactories.createPageTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wcc.platform.domain.cms.PageType;
import com.wcc.platform.domain.cms.pages.mentorship.FeedbackItem;
import com.wcc.platform.domain.cms.pages.mentorship.FeedbackSection;
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipPage;
import com.wcc.platform.utils.FileUtil;
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
        createPageTest(),
        createPageSectionTest("Mentor"),
        createPageSectionTest("Mentee"),
        createFeedbackSectionTest());
  }

  public static FeedbackItem createFeedbackItemTest(final boolean isMentor) {
    return new FeedbackItem("Person Name", "Nice feedback", isMentor, Year.of(2023));
  }

  public static FeedbackSection createFeedbackSectionTest() {
    return new FeedbackSection(
        "Feedback1", List.of(createFeedbackItemTest(true), createFeedbackItemTest(false)));
  }
}
