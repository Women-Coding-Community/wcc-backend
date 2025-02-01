package com.wcc.platform.domain.cms.pages;

import static com.wcc.platform.factories.SetupFactories.HERO_TITLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wcc.platform.factories.SetupFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LandingPageTest {

  private static final String VOLUNTEER_TITLE = "VolunteerTitle1";
  private LandingPage page1;
  private LandingPage page2;
  private LandingPage page3;

  @BeforeEach
  void setUp() {
    var hero = SetupFactories.createHeroSectionTest();
    var volunteer = SetupFactories.createPageTest(VOLUNTEER_TITLE);
    page1 = LandingPage.builder().heroSection(hero).build();
    page2 = LandingPage.builder().heroSection(hero).build();
    page3 = LandingPage.builder().heroSection(hero).volunteerSection(volunteer).build();
  }

  @Test
  void testEquals() {
    assertEquals(page2, page1);
    assertNotEquals(page2, page3);
  }

  @Test
  void testHashCode() {
    assertEquals(page2.hashCode(), page1.hashCode());
    assertNotEquals(page2.hashCode(), page3.hashCode());
  }

  @Test
  void testToString() {
    assertTrue(page1.toString().contains(HERO_TITLE));
    assertTrue(page2.toString().contains(HERO_TITLE));
    assertTrue(page3.toString().contains(VOLUNTEER_TITLE));
  }
}
