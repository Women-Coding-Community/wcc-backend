package com.wcc.platform.repository.postgresdb2;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wcc.platform.config.TestGoogleDriveConfig;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.factories.SetupMentorFactories;
import com.wcc.platform.repository.postgres.PostgresMemberRepository;
import com.wcc.platform.repository.postgres.PostgresMentorTestSetup;
import com.wcc.platform.repository.postgres.mentorship.PostgresMentorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
@Disabled("Temporary disable due to database compatibility issues")
class PostgresDb2MentorRepositoryIntegrationTest implements PostgresMentorTestSetup {

  private Mentor mentor;

  @Autowired private PostgresMentorRepository repository;
  @Autowired private PostgresMemberRepository memberRepository;

  @BeforeEach
  void setUp() {
    mentor = SetupMentorFactories.createMentorTest(2L, "Mentor DB2", "mentordb2_2@email.com");
    deleteMentor(mentor, repository, memberRepository);
  }

  @Test
  void testBasicCrud() {
    executeMentorCrud(mentor, repository, memberRepository);
    assertTrue(memberRepository.findById(42L).isEmpty());
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
