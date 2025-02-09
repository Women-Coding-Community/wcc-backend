package com.wcc.platform.factories;

import static com.wcc.platform.factories.SetupFactories.OBJECT_MAPPER;
import static com.wcc.platform.factories.SetupFactories.createNoImageHeroSectionTest;
import static com.wcc.platform.factories.SetupFactories.createPageSectionTest;
import static com.wcc.platform.factories.SetupFactories.createPageTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wcc.platform.domain.cms.PageType;
import com.wcc.platform.domain.cms.attributes.LabelLink;
import com.wcc.platform.domain.cms.pages.PageData;
import com.wcc.platform.domain.cms.pages.aboutUs.CelebrateHerPage;
import com.wcc.platform.domain.cms.pages.mentorship.FeedbackItem;
import com.wcc.platform.domain.cms.pages.mentorship.FeedbackSection;
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipPage;
import com.wcc.platform.domain.platform.AboutHer;
import com.wcc.platform.domain.platform.Event;
import com.wcc.platform.domain.platform.ProgramType;
import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.domain.platform.SocialNetworkType;
import com.wcc.platform.utils.FileUtil;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

/** Mentorship test factories. */
public class SetupCelebrateHerFactories {

  /** Test factory. */
  public static CelebrateHerPage createCelebrateHerPageTest(final String fileName) {
    try {
      final String content = FileUtil.readFileAsString(fileName);
      return OBJECT_MAPPER.readValue(content, CelebrateHerPage.class);
    } catch (JsonProcessingException e) {
      return createCelebrateHerPageTest();
    }
  }

  /** Test factory. */
  public static CelebrateHerPage createCelebrateHerPageTest() {
    final String pageId = PageType.CELEBRATE_HER.getId();
    return new CelebrateHerPage(
        pageId,
        createNoImageHeroSectionTest(),
        createPageTest(),
        Collections.singletonList(createEventTest()));
  }


  public static AboutHer createEventTest() {
    return AboutHer.builder()
        .hashtag("#celebrate_her")
        .listOfName(List.of("Liliia","Anna"))
        .description("Description")
        .postLink(new SocialNetwork(SocialNetworkType.LINKEDIN,"hello")).build();
  }
}
