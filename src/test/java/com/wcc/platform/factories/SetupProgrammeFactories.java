package com.wcc.platform.factories;

import static com.wcc.platform.factories.SetUpStyleFactories.createCustomStyleTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wcc.platform.domain.cms.attributes.CmsIcon;
import com.wcc.platform.domain.cms.attributes.ListSection;
import com.wcc.platform.domain.cms.pages.programme.ProgrammeItem;
import com.wcc.platform.domain.cms.pages.programme.ProgrammePage;
import com.wcc.platform.domain.platform.type.ProgramType;
import com.wcc.platform.utils.FileUtil;
import java.util.Collections;

/** Test factories for Programme. */
public class SetupProgrammeFactories {

  /**
   * Create a programme page with test data.
   *
   * @param fileName of a programme
   * @return Programme Page
   */
  public static ProgrammePage createProgrammePageTest(final String fileName) {
    try {
      final String content = FileUtil.readFileAsString(fileName);
      return SetupFactories.OBJECT_MAPPER.readValue(content, ProgrammePage.class);
    } catch (JsonProcessingException e) {
      return createProgrammePageTest();
    }
  }

  /**
   * Create a programme page with test data.
   *
   * @return Programme Page
   */
  public static ProgrammePage createProgrammePageTest() {
    return new ProgrammePage(
        "page:program",
        SetupFactories.createHeroSectionTest(),
        SetupFactories.createCommonSectionTest(),
        SetupFactories.createContactTest(),
        Collections.singletonList(createProgramme()),
        SetupEventFactories.createEventSection(),
        createCustomStyleTest());
  }

  /** Create Factory. */
  public static ListSection<String> createProgrammeByType(final ProgramType type) {
    if (ProgramType.BOOK_CLUB.equals(type)) {
      createProgramme();
    } else {
      return createProgrammeWithoutCard();
    }
    return null;
  }

  public static ListSection<String> createProgramme() {
    return new ListSection<>(
        "What We Are Reading",
        "Every month we vote we read a book this is current month book.",
        null,
        java.util.List.of("some value"));
  }

  /**
   * Create Programme object with test data.
   *
   * @return Programme object
   */
  public static ListSection<String> createProgrammeWithoutCard() {
    return new ListSection<>(
        "What We Are Reading",
        "Every month we vote we read a book this is current month book.",
        null,
        java.util.List.of());
  }

  /** Test factory. * */
  public static ProgrammeItem createProgrammeItemsTest(final ProgramType type, final CmsIcon icon) {
    return new ProgrammeItem(type, SetupFactories.createLinkTest(), icon);
  }
}
