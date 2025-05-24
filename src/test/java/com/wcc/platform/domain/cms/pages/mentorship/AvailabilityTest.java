package com.wcc.platform.domain.cms.pages.mentorship;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.Test;

class AvailabilityTest {

  @Test
  void testAvailabilityRecord() {
    List<Month> months = List.of(Month.of(5), Month.of(6), Month.of(7));
    Integer hours = 2;

    Availability availability = new Availability(months, hours);

    assertEquals("MAY", availability.months().get(0).toString());
    assertEquals("JUNE", availability.months().get(1).toString());
    assertEquals("JULY", availability.months().get(2).toString());
    assertEquals(hours, availability.hours());
  }
}
