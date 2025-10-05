package com.wcc.platform.repository.postgres;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.factories.SetupMentorshipFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/** Integration tests for PostgresMentorRepository using Testcontainers Postgres. */
class PostgresMentorRepositoryIntegrationTest extends DefaultDatabaseSetup {

  private Mentor mentor;

  @Autowired private PostgresMentorRepository repository;
  @Autowired private PostgresMemberRepository memberRepository;

  @BeforeEach
  void setUp() {
    mentor = SetupMentorshipFactories.createMentorTest();
    memberRepository.deleteByEmail(mentor.getEmail());
  }

  @Test
  void testBasicCrud() {
    mentor = repository.create(this.mentor);

    assertNotNull(mentor, "Should return mentor mentor");
    assertNotNull(mentor.getId(), "Created mentor must have an id");

    var found = repository.findById(mentor.getId());
    assertTrue(found.isPresent(), "Should find mentor mentor by id");
    assertEquals(this.mentor.getEmail(), found.get().getEmail(), "Email must match");
    assertEquals(
        this.mentor.getProfileStatus(),
        found.get().getProfileStatus(),
        "Profile status must match");

    var search = repository.findByEmail(this.mentor.getEmail());
    assertTrue(search.isPresent(), "Should find mentor mentor by email");

    var updated = repository.update(this.mentor.getId(), this.mentor);
    assertEquals(this.mentor, updated, "Exactly the same object because it is not implemented");

    repository.deleteById(this.mentor.getId());
    assertTrue(repository.getAll().isEmpty());
  }

  @Test
  void notFoundByEmail() {
    assertTrue(repository.findByEmail("invalid@mail.com").isEmpty());
  }

  @Test
  void notFoundIdByEmail() {
    assertNull(repository.findIdByEmail("invalid@mail.com"));
  }

  @Test
  void notFoundById() {
    assertTrue(repository.findById(42L).isEmpty());
  }
}
