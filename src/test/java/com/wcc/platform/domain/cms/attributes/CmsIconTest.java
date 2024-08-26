package com.wcc.platform.domain.cms.attributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class CmsIconTest {

  @Test
  void testToString() {
    assertEquals(CmsIcon.BOOK.toString(), CmsIcon.BOOK.getIconName());
  }

  @Test
  void testEquals() {
    assertEquals(CmsIcon.CODE, CmsIcon.CODE);
    assertNotEquals(CmsIcon.CALENDAR, CmsIcon.GROUP);
  }
}
