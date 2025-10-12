package com.wcc.platform.domain.cms.pages.mentorship;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Month;
import org.junit.jupiter.api.Test;

class MentorMonthAvailabilityTest {

  @Test
  void testAvailabilityRecord() {
    Month month = Month.of(5);
    Integer hours = 2;

    MentorMonthAvailability mentorMonthAvailability = new MentorMonthAvailability(month, hours);

    assertEquals("MAY", mentorMonthAvailability.month().toString());
    assertEquals(hours, mentorMonthAvailability.hours());
  }
}
