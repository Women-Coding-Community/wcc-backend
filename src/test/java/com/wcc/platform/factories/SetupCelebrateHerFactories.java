package com.wcc.platform.factories;

import static com.wcc.platform.factories.SetupFactories.OBJECT_MAPPER;
import static com.wcc.platform.factories.SetupFactories.createNoImageHeroSectionTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wcc.platform.domain.cms.PageType;
import com.wcc.platform.domain.cms.attributes.LabelLink;
import com.wcc.platform.domain.cms.pages.aboutus.CelebrateHerPage;
import com.wcc.platform.domain.platform.AboutHer;
import com.wcc.platform.utils.FileUtil;
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
        pageId, createNoImageHeroSectionTest(), Collections.singletonList(createEventTest()));
  }

  public static AboutHer createEventTest() {
    return AboutHer.builder()
        .hashtag("#celebrate_her")
        .listOfName(List.of("Liliia", "Anna"))
        .description("Description")
        .link(new LabelLink("linkedIn", "linkedIn", "https://linkedIn.com/lilrafil"))
        .build();
  }
}
