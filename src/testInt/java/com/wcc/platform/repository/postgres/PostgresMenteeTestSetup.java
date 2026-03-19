package com.wcc.platform.repository.postgres;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.MenteeRepository;
import com.wcc.platform.repository.postgres.mentorship.PostgresMenteeRepository;

/** Interface for default setup operations for Postgres Mentee repository. */
public interface PostgresMenteeTestSetup {

  default void deleteMentee(
      final Mentee mentee,
      final MenteeRepository repository,
      final MemberRepository memberRepository) {
    memberRepository.deleteByEmail(mentee.getEmail());
    repository.deleteById(mentee.getId());
  }

  /**
   * Tests basic CRUD (Create, Read, Update, Delete) operations for the Mentee entity using the
   * provided repository implementations.
   */
  default void executeMenteeCrud(
      final Mentee mentee,
      final PostgresMenteeRepository repository,
      final PostgresMemberRepository memberRepository) {
    var menteeCreated = repository.create(mentee);

    assertNotNull(menteeCreated, "Should return menteeCreated");
    assertNotNull(menteeCreated.getId(), "Created mentee must have an id");

    var found = repository.findById(menteeCreated.getId());
    assertTrue(found.isPresent(), "Should find mentee by id");

    var menteeFound = found.get();
    assertEquals(mentee.getEmail(), menteeFound.getEmail(), "Email must match");
    assertEquals(
        mentee.getProfileStatus(), menteeFound.getProfileStatus(), "Profile status must match");

    assertNotNull(menteeFound.getSkills(), "Skills should not be null");
    assertEquals(
        mentee.getSkills().yearsExperience(),
        menteeFound.getSkills().yearsExperience(),
        "Years of experience must match");
    assertEquals(
        mentee.getSkills().languages().size(),
        menteeFound.getSkills().languages().size(),
        "Number of languages must match");

    for (int i = 0; i < mentee.getSkills().languages().size(); i++) {
      assertEquals(
          mentee.getSkills().languages().get(i).proficiencyLevel(),
          menteeFound.getSkills().languages().get(i).proficiencyLevel(),
          "Language proficiency level must match");
    }

    assertEquals(
        mentee.getSkills().areas().size(),
        menteeFound.getSkills().areas().size(),
        "Number of technical areas must match");

    for (int i = 0; i < mentee.getSkills().areas().size(); i++) {
      assertEquals(
          mentee.getSkills().areas().get(i).proficiencyLevel(),
          menteeFound.getSkills().areas().get(i).proficiencyLevel(),
          "Technical area proficiency level must match");
    }
    assertEquals(
        mentee.getSkills().mentorshipFocus().size(),
        menteeFound.getSkills().mentorshipFocus().size(),
        "Number of focus areas must match");

    repository.deleteById(menteeCreated.getId());
    assertTrue(repository.findById(menteeCreated.getId()).isEmpty());

    memberRepository.deleteById(menteeCreated.getId());
    assertTrue(memberRepository.findById(menteeCreated.getId()).isEmpty());
  }
}
