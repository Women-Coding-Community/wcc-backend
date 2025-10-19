package com.wcc.platform.repository.postgresdb2;

import com.wcc.platform.config.TestGoogleDriveConfig;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.factories.SetupMentorshipFactories;
import com.wcc.platform.repository.postgres.PostgresMemberRepository;
import com.wcc.platform.repository.postgres.PostgresMentorRepository;
import com.wcc.platform.repository.postgres.PostgresMentorTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/** Integration test simulating DB2 by using H2 (PostgreSQL mode) through the test-db2 profile. */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(TestGoogleDriveConfig.class)
@ActiveProfiles("test-db2")
class PostgresDb2MentorRepositoryIntegrationTest implements PostgresMentorTestSetup {

  private Mentor mentor;

  @Autowired private PostgresMentorRepository repository;
  @Autowired private PostgresMemberRepository memberRepository;

  @BeforeEach
  void setUp() {
    mentor = SetupMentorshipFactories.createMentorTest(2L, "Mentor DB2", "mentordb2_2@email.com");
    deleteAll(mentor, repository, memberRepository);
  }

  @Test
  void testBasicCrud() {
    executeMentorCrud(mentor, repository, memberRepository);
  }

  @Test
  void notFoundIdByEmail() {
    notFoundIdByEmail(repository, "invalid@mail.com");
  }

  @Test
  void notFoundById() {
    notFoundById(repository, 42L);
  }
}
