package com.wcc.platform.domain.cms.pages.programme;

import static com.wcc.platform.factories.SetupFactories.createContactTest;
import static org.junit.jupiter.api.Assertions.*;

import com.wcc.platform.domain.cms.attributes.Contact;
import com.wcc.platform.domain.cms.pages.Page;
import com.wcc.platform.domain.platform.EventSection;
import com.wcc.platform.domain.platform.Programme;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProgrammePageTest {

  private final Page page = new Page();
  private final Contact contact = createContactTest();
  private final List<Programme> programmeDetails = List.of(new Programme());
  private final EventSection eventSection = new EventSection();

  @Test
  @DisplayName("Given no-args constructor, when initialized, then fields should be null")
  void noArgsConstructor() {
    ProgrammePage programmePage = new ProgrammePage();

    assertNull(programmePage.getPage());
    assertNull(programmePage.getContact());
    assertNull(programmePage.getProgrammeDetails());
    assertNull(programmePage.getEventSection());
  }

  @Test
  @DisplayName(
      "Given all-args constructor, when initialized, then fields should be correctly assigned")
  void allArgsConstructor() {
    ProgrammePage programmePage = new ProgrammePage(page, contact, programmeDetails, eventSection);

    assertEquals(page, programmePage.getPage());
    assertEquals(contact, programmePage.getContact());
    assertEquals(programmeDetails, programmePage.getProgrammeDetails());
    assertEquals(eventSection, programmePage.getEventSection());
  }

  @Test
  @DisplayName("Given builder, when fields are set, then fields should be correctly assigned")
  void builder() {
    ProgrammePage programmePage =
        ProgrammePage.builder()
            .page(page)
            .contact(contact)
            .programmeDetails(programmeDetails)
            .eventSection(eventSection)
            .build();

    assertEquals(page, programmePage.getPage());
    assertEquals(contact, programmePage.getContact());
    assertEquals(programmeDetails, programmePage.getProgrammeDetails());
    assertEquals(eventSection, programmePage.getEventSection());
  }

  @Test
  @DisplayName(
      "Given two identical ProgrammePage objects, "
          + "when compared, then they should be equal and hash codes should match")
  void equalsAndHashCode() {
    ProgrammePage programmePage1 = new ProgrammePage(page, contact, programmeDetails, eventSection);
    ProgrammePage programmePage2 = new ProgrammePage(page, contact, programmeDetails, eventSection);

    assertEquals(programmePage1, programmePage2);
    assertEquals(programmePage1.hashCode(), programmePage2.hashCode());
  }

  @Test
  @DisplayName(
      "Given ProgrammePage, when toString is called, "
          + "then it should return correct string representation")
  void toStringTest() {
    ProgrammePage programmePage = new ProgrammePage(page, contact, programmeDetails, eventSection);
    String expected =
        "ProgrammePage(page="
            + page
            + ", contact="
            + contact
            + ", programmeDetails="
            + programmeDetails
            + ", eventSection="
            + eventSection
            + ")";

    assertEquals(expected, programmePage.toString());
  }
}
