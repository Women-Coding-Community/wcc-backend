package com.wcc.platform.repository.postgres;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.factories.SetupMentorFactories;
import com.wcc.platform.repository.postgres.mentorship.PostgresMentorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/** Integration tests for PostgresMentorRepository using Testcontainers Postgres. */
class PostgresMentorRepositoryIntegrationTest extends DefaultDatabaseSetup
    implements PostgresMentorTestSetup {

  private Mentor mentor;

  @Autowired private PostgresMentorRepository repository;
  @Autowired private PostgresMemberRepository memberRepository;

  @BeforeEach
  void setUp() {
    mentor = SetupMentorFactories.createMentorTest(14L, "Mentor 14", "mentor14@email.com");
    deleteAll(mentor, repository, memberRepository);
  }

  @Test
  void testBasicCrud() {
    executeMentorCrud(mentor, repository, memberRepository);
    assertTrue(memberRepository.findById(mentor.getId()).isEmpty());
  }

  @Test
  void notFoundIdByEmail() {
    assertNull(repository.findIdByEmail("mentor13@mail.com"));
  }

  @Test
  void notFoundById() {
    assertTrue(repository.findById(13L).isEmpty());
  }
}
