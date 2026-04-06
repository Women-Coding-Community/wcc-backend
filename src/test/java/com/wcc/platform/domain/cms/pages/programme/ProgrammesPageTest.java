package com.wcc.platform.domain.cms.pages.programme;

import static com.wcc.platform.factories.SetUpProgrammesFactories.createProgrammesSectionTest;
import static com.wcc.platform.factories.SetupFactories.createHeroSectionTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.wcc.platform.domain.cms.attributes.CommonSection;
import com.wcc.platform.domain.cms.attributes.HeroSection;
import com.wcc.platform.domain.cms.attributes.ListSection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProgrammesPageTest {

  public static final String PAGE_ID = "page:PROGRAMMES";
  private final HeroSection heroSection = createHeroSectionTest();
  private final CommonSection section = new CommonSection();
  private final ListSection<ProgrammeCardItem> programmesSection = createProgrammesSectionTest();

  @Test
  @DisplayName("Given no-args constructor, when initialized, then fields should be null")
  void noArgsConstructor() {
    ProgrammesPage programmesPage = new ProgrammesPage();
    assertNull(programmesPage.getHeroSection());
    assertNull(programmesPage.getSection());
    assertNull(programmesPage.getProgrammesSection());
  }

  @Test
  @DisplayName(
      "Given all-args constructor, when initialized, then fields should be correctly assigned")
  void allArgsConstructor() {
    ProgrammesPage programmesPage =
        new ProgrammesPage(PAGE_ID, heroSection, section, programmesSection);

    assertEquals(heroSection, programmesPage.getHeroSection());
    assertEquals(section, programmesPage.getSection());
    assertEquals(programmesSection, programmesPage.getProgrammesSection());
  }

  @Test
  @DisplayName("Given builder, when fields are set, then fields should be correctly assigned")
  void builder() {
    ProgrammesPage programmesPage =
        ProgrammesPage.builder()
            .id(PAGE_ID)
            .heroSection(heroSection)
            .section(section)
            .programmesSection(programmesSection)
            .build();

    assertEquals(heroSection, programmesPage.getHeroSection());
    assertEquals(section, programmesPage.getSection());
    assertEquals(programmesSection, programmesPage.getProgrammesSection());
  }

  @Test
  @DisplayName(
      "Given two identical ProgrammesPage objects, when compared, then they should be equal and hash codes should match")
  void equalsAndHashCode() {
    ProgrammesPage programmesPage1 =
        new ProgrammesPage(PAGE_ID, heroSection, section, programmesSection);
    ProgrammesPage programmesPage2 =
        new ProgrammesPage(PAGE_ID, heroSection, section, programmesSection);

    assertEquals(programmesPage1, programmesPage2);
    assertEquals(programmesPage1.hashCode(), programmesPage2.hashCode());
  }

  @Test
  @DisplayName(
      "Given ProgrammesPage, when toString is called, then it should return correct string representation")
  void testToString() {
    ProgrammesPage programmesPage =
        new ProgrammesPage(PAGE_ID, heroSection, section, programmesSection);
    String expected =
        "ProgrammesPage(id="
            + PAGE_ID
            + ", heroSection="
            + heroSection
            + ", section="
            + section
            + ", programmesSection="
            + programmesSection
            + ")";

    assertEquals(expected, programmesPage.toString());
  }
}
