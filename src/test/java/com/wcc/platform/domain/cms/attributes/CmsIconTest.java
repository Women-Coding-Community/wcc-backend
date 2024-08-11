package com.wcc.platform.domain.cms.attributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class CmsIconTest {

  @Test
  void testToString() {
    assertEquals(CmsIcon.ICON_2.toString(), CmsIcon.ICON_2.getClassName());
  }

  @Test
  void testEquals() {
    assertEquals(CmsIcon.ICON_2, CmsIcon.ICON_2);
    assertNotEquals(CmsIcon.ICON_1, CmsIcon.ICON_2);
  }
}
