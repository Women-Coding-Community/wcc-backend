package com.wcc.platform.domain.cms.attributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class ProficiencyLevelTest {

  @Test
  void testFromIdValid() {
    assertEquals(ProficiencyLevel.BEGINNER, ProficiencyLevel.fromId(1));
    assertEquals(ProficiencyLevel.INTERMEDIATE, ProficiencyLevel.fromId(2));
    assertEquals(ProficiencyLevel.ADVANCED, ProficiencyLevel.fromId(3));
    assertEquals(ProficiencyLevel.EXPERT, ProficiencyLevel.fromId(4));
  }

  @Test
  void testFromIdInvalidThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> ProficiencyLevel.fromId(-1));
    assertThrows(IllegalArgumentException.class, () -> ProficiencyLevel.fromId(99));
  }

  @Test
  void testGetAll() {
    List<ProficiencyLevel> all = ProficiencyLevel.getAll();
    assertEquals(4, all.size());
    assertTrue(all.contains(ProficiencyLevel.BEGINNER));
    assertTrue(all.contains(ProficiencyLevel.INTERMEDIATE));
    assertTrue(all.contains(ProficiencyLevel.ADVANCED));
    assertTrue(all.contains(ProficiencyLevel.EXPERT));
  }

  @Test
  void testToString() {
    assertEquals("BEGINNER", ProficiencyLevel.BEGINNER.toString());
    assertEquals("INTERMEDIATE", ProficiencyLevel.INTERMEDIATE.toString());
    assertEquals("ADVANCED", ProficiencyLevel.ADVANCED.toString());
    assertEquals("EXPERT", ProficiencyLevel.EXPERT.toString());
  }

  @Test
  void testGetDescription() {
    assertEquals("Beginner", ProficiencyLevel.BEGINNER.getDescription());
  }

  @Test
  void testGetLevelId() {
    assertEquals(1, ProficiencyLevel.BEGINNER.getLevelId());
  }
}
