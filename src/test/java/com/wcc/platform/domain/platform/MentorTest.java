package com.wcc.platform.domain.platform;

import static com.wcc.platform.factories.SetupMentorshipFactories.createMentorTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    assertTrue(mentor.toString().contains(MemberType.MENTOR.toString()));
  }
}
