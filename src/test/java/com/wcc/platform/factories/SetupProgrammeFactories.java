package com.wcc.platform.factories;

import static com.wcc.platform.factories.SetupEventFactories.createEventSection;
import static com.wcc.platform.factories.SetupFactories.OBJECT_MAPPER;
import static com.wcc.platform.factories.SetupFactories.createContactTest;
import static com.wcc.platform.factories.SetupFactories.createPageTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wcc.platform.domain.cms.attributes.Card;
import com.wcc.platform.domain.cms.attributes.ProgramType;
import com.wcc.platform.domain.cms.attributes.SimpleLink;
import com.wcc.platform.domain.cms.pages.programme.ProgrammePage;
import com.wcc.platform.domain.platform.Programme;
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
      return OBJECT_MAPPER.readValue(content, ProgrammePage.class);
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
        createPageTest(),
        createContactTest(),
        Collections.singletonList(createProgramme()),
        Collections.singletonList(createEventSection()));
  }

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
            new Card(
                "Test book title",
                "Author of the book",
                "test book description",
                new SimpleLink("Good read", "htpp/link")))
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
}
