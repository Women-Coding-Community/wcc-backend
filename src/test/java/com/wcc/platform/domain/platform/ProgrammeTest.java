package com.wcc.platform.domain.platform;

import static com.wcc.platform.factories.SetupProgrammeFactories.createProgramme;
import static com.wcc.platform.factories.SetupProgrammeFactories.createProgrammeByType;
import static com.wcc.platform.factories.SetupProgrammeFactories.createProgrammeWithoutCard;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Test class for {@link Programme}. */
class ProgrammeTest {

  private final UUID uuid = UUID.randomUUID();

  Programme testProgramme;

  @BeforeEach
  void setup() {
    testProgramme = createProgramme(uuid);
  }

  @Test
  void testEquals() {
    assertEquals(testProgramme, createProgramme(uuid));
  }

  @Test
  void testNotEquals() {
    assertNotEquals(testProgramme, createProgrammeByType(ProgramType.TECH_TALK));
  }

  @Test
  void testHashCode() {
    assertEquals(testProgramme.hashCode(), createProgramme(uuid).hashCode());
  }

  @Test
  void testHashCodeNotEquals() {
    assertNotEquals(testProgramme.hashCode(), createProgrammeWithoutCard().hashCode());
  }
}
