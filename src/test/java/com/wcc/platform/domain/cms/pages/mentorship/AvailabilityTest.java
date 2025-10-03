package com.wcc.platform.domain.cms.pages.mentorship;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Month;
import org.junit.jupiter.api.Test;

class AvailabilityTest {

  @Test
  void testAvailabilityRecord() {
    Month month = Month.of(5);
    Integer hours = 2;

    Availability availability = new Availability(month, hours);

    assertEquals("MAY", availability.month().toString());
    assertEquals(hours, availability.hours());
  }
}
