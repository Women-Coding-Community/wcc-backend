package com.wcc.platform.domain.platform;

import static com.wcc.platform.factories.SetupMentorFactories.createMentorTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.wcc.platform.domain.platform.mentorship.Mentor;
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
    final var expected =
        "Mentor(profileStatus=ACTIVE, skills=Skills[yearsExperience=2, areas=[Backend, Frontend], "
            + "languages=[Javascript], mentorshipFocus=[Grow from beginner to mid-level]], "
            + "spokenLanguages=[English], bio=Mentor bio, "
            + "menteeSection=MenteeSection[mentorshipType=[Long-Term], "
            + "availability=[MentorMonthAvailability[month=APRIL, hours=2]], "
            + "idealMentee=ideal mentee description, "
            + "additional=additional], feedbackSection=null, resources=null)";
    assertEquals(expected, mentor.toString());
  }
}
