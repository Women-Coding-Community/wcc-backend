package com.wcc.platform.domain.platform.mentorship;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MentorAvailabilityTest {

  @Test
  void mentorshipType() {
    var availability = new MentorAvailability(MentorshipType.AD_HOC, true);
    assertEquals(MentorshipType.AD_HOC, availability.mentorshipType());
  }

  @Test
  void available() {
    var availability = new MentorAvailability(MentorshipType.AD_HOC, false);
    assertFalse(availability.available());
  }
}
