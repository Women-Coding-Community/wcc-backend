package com.wcc.platform.domain.cms.attributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class ProficiencyLevelTest {

  @Test
  void testFromIdValid() {
    assertEquals(ProficiencyLevel.UNDEFINED, ProficiencyLevel.fromId(0));
    assertEquals(ProficiencyLevel.BEGINNER, ProficiencyLevel.fromId(1));
    assertEquals(ProficiencyLevel.INTERMEDIATE, ProficiencyLevel.fromId(2));
    assertEquals(ProficiencyLevel.ADVANCED, ProficiencyLevel.fromId(3));
    assertEquals(ProficiencyLevel.EXPERT, ProficiencyLevel.fromId(4));
  }

  @Test
  void testFromIdInvalidReturnsBeginner() {
    // Current implementation returns BEGINNER for unknown IDs
    assertEquals(ProficiencyLevel.BEGINNER, ProficiencyLevel.fromId(-1));
    assertEquals(ProficiencyLevel.BEGINNER, ProficiencyLevel.fromId(99));
  }

  @Test
  void testGetAll() {
    List<ProficiencyLevel> all = ProficiencyLevel.getAll();
    assertEquals(5, all.size());
    assertTrue(all.contains(ProficiencyLevel.UNDEFINED));
    assertTrue(all.contains(ProficiencyLevel.BEGINNER));
    assertTrue(all.contains(ProficiencyLevel.INTERMEDIATE));
    assertTrue(all.contains(ProficiencyLevel.ADVANCED));
    assertTrue(all.contains(ProficiencyLevel.EXPERT));
  }

  @Test
  void testToString() {
    assertEquals("N/A", ProficiencyLevel.UNDEFINED.toString());
    assertEquals("Beginner", ProficiencyLevel.BEGINNER.toString());
    assertEquals("Intermediate", ProficiencyLevel.INTERMEDIATE.toString());
    assertEquals("Advanced", ProficiencyLevel.ADVANCED.toString());
    assertEquals("Expert", ProficiencyLevel.EXPERT.toString());
  }

  @Test
  void testGetDescription() {
    assertEquals("N/A", ProficiencyLevel.UNDEFINED.getDescription());
    assertEquals("Beginner", ProficiencyLevel.BEGINNER.getDescription());
  }

  @Test
  void testGetLevelId() {
    assertEquals(0, ProficiencyLevel.UNDEFINED.getLevelId());
    assertEquals(1, ProficiencyLevel.BEGINNER.getLevelId());
  }
}
