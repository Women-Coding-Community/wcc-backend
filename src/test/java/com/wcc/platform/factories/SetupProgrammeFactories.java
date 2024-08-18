package com.wcc.platform.factories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wcc.platform.domain.cms.attributes.CmsIcon;
import com.wcc.platform.domain.cms.attributes.LabelLink;
import com.wcc.platform.domain.cms.pages.Page;
import com.wcc.platform.domain.cms.pages.programme.ProgrammeItem;
import com.wcc.platform.domain.cms.pages.programme.ProgrammePage;
import com.wcc.platform.domain.platform.ProgramType;
import com.wcc.platform.domain.platform.Programme;
import com.wcc.platform.utils.FileUtil;
import java.util.Collections;
import java.util.List;

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
        SetupFactories.createPageTest(),
        SetupFactories.createContactTest(),
        Collections.singletonList(createProgramme()),
        SetupEventFactories.createEventSection());
  }

  /** Create Factory. */
  public static Programme createProgrammeByType(final ProgramType type) {
    if (ProgramType.BOOK_CLUB.equals(type)) {
      createProgramme();
    } else {
      return createProgrammeWithoutCard();
    }
    return null;
  }

  /**
   * Create Programme object with test data.
   *
   * @return Programme object
   */
  public static Programme createProgramme() {
    return Programme.builder()
        .title("What We Are Reading")
        .description("Every month we vote we read a book this is current month book.")
        .card(
            new Page(
                "Test book title",
                "Author of the book",
                "test book description",
                new LabelLink("Title Link", "Good read", "http://link"),
                List.of()))
        .build();
  }

  /**
   * Create Programme object with test data.
   *
   * @return Programme object
   */
  public static Programme createProgrammeWithoutCard() {
    return Programme.builder()
        .title("What We Are Reading")
        .description("Every month we vote we read a book this is current month book.")
        .build();
  }

  /** Test factory. * */
  public static ProgrammeItem createProgrammeItemsTest(final ProgramType type, final CmsIcon icon) {
    return new ProgrammeItem(type, SetupFactories.createLinkTest(), icon);
  }
}
