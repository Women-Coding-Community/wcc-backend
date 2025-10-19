package com.wcc.platform.repository.postgres;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wcc.platform.domain.platform.mentorship.Mentor;

/** Interface for default setup operations for Postgres repositories. */
public interface PostgresMentorTestSetup {

  default void deleteAll(
      Mentor mentor,
      PostgresMentorRepository repository,
      PostgresMemberRepository memberRepository) {
    memberRepository.deleteByEmail(mentor.getEmail());
    repository.deleteById(mentor.getId());
  }

  /**
   * Tests basic CRUD (Create, Read, Update, Delete) operations for the Mentor entity using the
   * provided repository implementations.
   */
  default void executeMentorCrud(
      Mentor mentor,
      PostgresMentorRepository repository,
      PostgresMemberRepository memberRepository) {
    var mentorCreated = repository.create(mentor);

    assertNotNull(mentorCreated, "Should return mentorCreated mentorCreated");
    assertNotNull(mentorCreated.getId(), "Created mentorCreated must have an id");

    var found = repository.findById(mentorCreated.getId());
    assertTrue(found.isPresent(), "Should find mentorCreated mentorCreated by id");

    var mentorFound = found.get();
    assertEquals(mentor.getEmail(), mentorFound.getEmail(), "Email must match");
    assertEquals(
        mentor.getProfileStatus(), mentorFound.getProfileStatus(), "Profile status must match");

    var search = repository.findByEmail(mentor.getEmail());
    assertTrue(search.isPresent(), "Should find mentorCreated mentorCreated by email");

    repository.deleteById(mentor.getId());
    assertTrue(repository.findById(mentor.getId()).isEmpty());

    memberRepository.deleteById(mentor.getId());
    assertTrue(memberRepository.findById(mentor.getId()).isEmpty());
  }

  default void notFoundIdByEmail(PostgresMentorRepository repository, String email) {
    assertNull(repository.findIdByEmail(email));
  }

  default void notFoundById(PostgresMentorRepository repository, Long id) {
    assertTrue(repository.findById(id).isEmpty());
  }
}
