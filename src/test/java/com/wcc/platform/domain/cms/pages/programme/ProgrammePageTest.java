package com.wcc.platform.domain.cms.pages.programme;

import static com.wcc.platform.factories.SetUpStyleFactories.createCustomStyleTest;
import static com.wcc.platform.factories.SetupFactories.createContactTest;
import static com.wcc.platform.factories.SetupFactories.createHeroSectionTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.wcc.platform.domain.cms.attributes.CommonSection;
import com.wcc.platform.domain.cms.attributes.Contact;
import com.wcc.platform.domain.cms.attributes.HeroSection;
import com.wcc.platform.domain.cms.attributes.ListSection;
import com.wcc.platform.domain.cms.attributes.style.CustomStyle;
import com.wcc.platform.domain.cms.pages.events.EventSection;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProgrammePageTest {

  public static final String PROG_ID = "1";
  private final HeroSection heroSection = createHeroSectionTest();
  private final CommonSection section = new CommonSection();
  private final Contact contact = createContactTest();
  private final List<ListSection<String>> programmeDetails =
      List.of(
          new ListSection<>(
              "title", "description", null, java.util.List.of("item1", "item2")));
  private final EventSection eventSection = new EventSection();
  private final CustomStyle customStyle = createCustomStyleTest();

  @Test
  @DisplayName("Given no-args constructor, when initialized, then fields should be null")
  void noArgsConstructor() {
    ProgrammePage programmePage = new ProgrammePage();
    assertNull(programmePage.getHeroSection());
    assertNull(programmePage.getSection());
    assertNull(programmePage.getContact());
    assertNull(programmePage.getProgrammeDetails());
    assertNull(programmePage.getEventSection());
  }

  @Test
  @DisplayName(
      "Given all-args constructor, when initialized, then fields should be correctly assigned")
  void allArgsConstructor() {
    ProgrammePage programmePage =
        new ProgrammePage(
            "id", heroSection, section, contact, programmeDetails, eventSection, customStyle);

    assertEquals(heroSection, programmePage.getHeroSection());
    assertEquals(section, programmePage.getSection());
    assertEquals(contact, programmePage.getContact());
    assertEquals(programmeDetails, programmePage.getProgrammeDetails());
    assertEquals(eventSection, programmePage.getEventSection());
  }

  @Test
  @DisplayName("Given builder, when fields are set, then fields should be correctly assigned")
  void builder() {
    ProgrammePage programmePage =
        ProgrammePage.builder()
            .heroSection(heroSection)
            .section(section)
            .contact(contact)
            .programmeDetails(programmeDetails)
            .eventSection(eventSection)
            .build();

    assertEquals(heroSection, programmePage.getHeroSection());
    assertEquals(section, programmePage.getSection());
    assertEquals(contact, programmePage.getContact());
    assertEquals(programmeDetails, programmePage.getProgrammeDetails());
    assertEquals(eventSection, programmePage.getEventSection());
  }

  @Test
  @DisplayName(
      "Given two identical ProgrammePage objects, "
          + "when compared, then they should be equal and hash codes should match")
  void equalsAndHashCode() {
    ProgrammePage programmePage1 =
        new ProgrammePage(
            PROG_ID, heroSection, section, contact, programmeDetails, eventSection, customStyle);
    ProgrammePage programmePage2 =
        new ProgrammePage(
            PROG_ID, heroSection, section, contact, programmeDetails, eventSection, customStyle);

    assertEquals(programmePage1, programmePage2);
    assertEquals(programmePage1.hashCode(), programmePage2.hashCode());
  }

  @Test
  @DisplayName(
      "Given ProgrammePage, when toString is called, "
          + "then it should return correct string representation")
  void testToString() {
    ProgrammePage programmePage =
        new ProgrammePage(
            PROG_ID, heroSection, section, contact, programmeDetails, eventSection, customStyle);
    String expected =
        "ProgrammePage(id="
            + PROG_ID
            + ", heroSection="
            + heroSection
            + ", section="
            + section
            + ", contact="
            + contact
            + ", programmeDetails="
            + programmeDetails
            + ", eventSection="
            + eventSection
            + ", customStyle="
            + customStyle
            + ")";

    assertEquals(expected, programmePage.toString());
  }
}
