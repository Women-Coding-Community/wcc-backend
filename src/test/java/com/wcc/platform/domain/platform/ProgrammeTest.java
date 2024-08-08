package com.wcc.platform.domain.platform;

import static com.wcc.platform.factories.SetupProgrammeFactories.createProgramme;
import static com.wcc.platform.factories.SetupProgrammeFactories.createProgrammeByType;
import static com.wcc.platform.factories.SetupProgrammeFactories.createProgrammeWithoutCard;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.wcc.platform.domain.cms.attributes.ProgramType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProgrammeTest {

  Programme testProgramme;

  @BeforeEach
  void setup() {
    testProgramme = createProgramme();
  }

  @Test
  void testEquals() {
    assertEquals(testProgramme, createProgramme());
  }

  @Test
  void testNotEquals() {
    assertNotEquals(testProgramme, createProgrammeByType(ProgramType.TECH_TALK));
  }

  @Test
  void testHashCode() {
    assertEquals(testProgramme.hashCode(), createProgramme().hashCode());
  }

  @Test
  void testHashCodeNotEquals() {
    assertNotEquals(testProgramme.hashCode(), createProgrammeWithoutCard().hashCode());
  }
}
