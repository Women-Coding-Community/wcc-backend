package com.wcc.platform.repository.postgresdb2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wcc.platform.config.TestGoogleDriveConfig;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.factories.SetupMentorshipFactories;
import com.wcc.platform.repository.postgres.PostgresMemberRepository;
import com.wcc.platform.repository.postgres.PostgresMentorRepository;
import java.util.List;
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
class PostgresDb2MentorRepositoryIntegrationTest {

  private final Mentor mentor = SetupMentorshipFactories.createMentorTest();

  @Autowired private PostgresMentorRepository repository;
  @Autowired private PostgresMemberRepository memberRepository;

  @BeforeEach
  void setUp() {
    memberRepository.deleteByEmail(mentor.getEmail());
  }

  @Test
  void createShouldPersistMentorAndBeRetrievable() {
    final Mentor toCreate = SetupMentorshipFactories.createMentorTest();

    final Mentor created = repository.create(toCreate);

    assertNotNull(created, "Should return created mentor");
    assertNotNull(created.getId(), "Created mentor must have an id");

    var found = repository.findById(created.getId());
    assertTrue(found.isPresent(), "Should find created mentor by id");
    assertEquals(toCreate.getEmail(), found.get().getEmail(), "Email must match");
  }

  @Test
  void getAllShouldContainCreatedMentor() {
    final Mentor toCreate = SetupMentorshipFactories.createMentorTest();
    final Mentor created = repository.create(toCreate);

    final List<Mentor> all = repository.getAll();

    assertTrue(
        all.stream().anyMatch(m -> m.getId().equals(created.getId())),
        "getAll must contain the created mentor");
  }
}
