package com.wcc.platform.factories;

import static com.wcc.platform.factories.TestFactories.createPageSectionTest;
import static com.wcc.platform.factories.TestFactories.createPageTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wcc.platform.domain.cms.pages.mentorship.FeedbackItem;
import com.wcc.platform.domain.cms.pages.mentorship.FeedbackSection;
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipPage;
import com.wcc.platform.utils.FileUtil;
import java.time.Year;
import java.util.List;

/** Mentorship test factories. */
public class TestMentorshipFactories {

  /** Test factory. */
  public static MentorshipPage createMentorshipPageTest(String fileName) {
    try {
      String content = FileUtil.readFileAsString(fileName);
      return ObjectMapperTestFactory.getInstance().readValue(content, MentorshipPage.class);
    } catch (JsonProcessingException e) {
      return createMentorshipPageTest();
    }
  }

  /** Test factory. */
  public static MentorshipPage createMentorshipPageTest() {
    return new MentorshipPage(
        createPageTest(),
        createPageSectionTest("Mentor"),
        createPageSectionTest("Mentee"),
        createFeedbackSectionTest());
  }

  public static FeedbackItem createFeedbackItemTest(boolean isMentor) {
    return new FeedbackItem("Person Name", "Nice feedback", isMentor, Year.of(2023));
  }

  public static FeedbackSection createFeedbackSectionTest() {
    return new FeedbackSection(
        "Feedback1", List.of(createFeedbackItemTest(true), createFeedbackItemTest(false)));
  }
}
