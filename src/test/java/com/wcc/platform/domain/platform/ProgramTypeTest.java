package com.wcc.platform.domain.platform;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Test class for {@link ProgramType}. */
class ProgramTypeTest {

  @Test
  void shouldReturnCorrectDescriptionForEnumValues() {
    // When & Then: Check that each enum value has the correct description
    assertEquals("Book Club", ProgramType.BOOK_CLUB.getDescription());
    assertEquals("Coding Club Python", ProgramType.CODING_CLUB.getDescription());
    assertEquals("Career Club", ProgramType.CAREER_CLUB.getDescription());
    assertEquals("Speaking Club", ProgramType.SPEAKING_CLUB.getDescription());
    assertEquals("Writing Club", ProgramType.WRITING_CLUB.getDescription());
    assertEquals("Cloud and DevOps", ProgramType.CLOUD.getDescription());
    assertEquals("Machine Learning", ProgramType.MACHINE_LEARNING.getDescription());
    assertEquals("Interview Preparation", ProgramType.INTERVIEW_PREP.getDescription());
    assertEquals("Others", ProgramType.OTHERS.getDescription());
    assertEquals("Tech Talk", ProgramType.TECH_TALK.getDescription());
  }
}
