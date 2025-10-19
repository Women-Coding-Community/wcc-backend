package com.wcc.platform.repository.postgres;

import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.factories.SetupMentorshipFactories;
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
    mentor = SetupMentorshipFactories.createMentorTest(14L, "Mentor 14", "mentor14@email.com");
    deleteAll(mentor, repository, memberRepository);
  }

  @Test
  void testBasicCrud() {
    executeMentorCrud(mentor, repository, memberRepository);
  }

  @Test
  void notFoundIdByEmail() {
    notFoundIdByEmail(repository, "mentor13@mail.com");
  }

  @Test
  void notFoundById() {
    notFoundById(repository, 13L);
  }
}
