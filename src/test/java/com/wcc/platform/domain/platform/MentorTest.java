package com.wcc.platform.domain.platform;

import static com.wcc.platform.factories.SetupMentorFactories.createMentorTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.wcc.platform.domain.platform.mentorship.Mentor;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/* Test for class {@link Mentor}. */
class MentorTest {

  private Mentor mentor;

  @BeforeEach
  void setUp() {
    mentor = createMentorTest();
  }

  @Test
  void testEquals() {
    assertEquals(mentor, createMentorTest());
  }

  @Test
  void testNotEquals() {
    assertNotEquals(mentor, createMentorTest("mentor2"));
  }

  @Test
  void testHashCode() {
    assertEquals(mentor.hashCode(), createMentorTest().hashCode());
  }

  @Test
  void testHashCodeNotEquals() {
    assertNotEquals(mentor.hashCode(), createMentorTest("mentor2").hashCode());
  }

  @Test
  void testToString() {
    final var result = mentor.toString();
    assertThat(result).contains("profileStatus=PENDING");
    assertThat(result).contains("bio=Mentor bio");
    assertThat(result).contains("spokenLanguages=[English, Spanish, German]");
    assertThat(result).contains("menteeSection=MenteeSection");
  }

  @Test
  void testSpokenLanguagesAreCapitalized() {
    var mentor = createMentorTest(1L, "test name", "test@email.com");

    assertEquals(List.of("English", "Spanish", "German"), mentor.getSpokenLanguages());
  }
}
