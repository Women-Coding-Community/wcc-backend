package com.wcc.platform.domain.platform.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MentorResourceTypeTest {

  @Test
  void mentorResourceTypeEnumShouldHaveValues() {
    MentorResourceType[] types = MentorResourceType.values();
    assertNotNull(types, "MentorResourceType enum should have values");
    assertTrue(types.length > 0, "MentorResourceType enum should contain at least one value");
  }

  @Test
  void testGetResourceTypeId() {
    assertEquals(2, MentorResourceType.LINKS.getTypeId());
  }
}
