package com.wcc.platform.repository.postgres;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.factories.SetupMenteeFactories;
import com.wcc.platform.repository.postgres.mentorship.PostgresMenteeRepository;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/** Integration tests for PostgresMenteeRepository using Testcontainers Postgres. */
class PostgresMenteeRepositoryIntegrationTest extends DefaultDatabaseSetup
    implements PostgresMenteeTestSetup {

  private Mentee mentee;

  @Autowired private PostgresMenteeRepository repository;
  @Autowired private PostgresMemberRepository memberRepository;

  @BeforeEach
  void setUp() {
    mentee = SetupMenteeFactories.createMenteeTest(15L, "Mentee 15", "mentee15@email.com");
    deleteMentee(mentee, repository, memberRepository);
  }

  @Test
  void testBasicCrud() {
    executeMenteeCrud(mentee, repository, memberRepository);
    assertTrue(memberRepository.findById(mentee.getId()).isEmpty());
  }

  @Test
  void testGetAll() {
    repository.create(mentee);
    assertThat(repository.getAll()).isNotEmpty();
    repository.deleteById(mentee.getId());
    memberRepository.deleteById(mentee.getId());
  }

  @Test
  void notFoundById() {
    assertTrue(repository.findById(999L).isEmpty());
  }

  @Test
  void testCreateInvalidMenteeThrowsException() {
    var invalidMentee =
        Mentee.menteeBuilder()
            .fullName("") // Invalid: @NotBlank
            .email("invalid-email") // Invalid: @Email
            .spokenLanguages(List.of())
            .build();

    assertThrows(ConstraintViolationException.class, () -> repository.create(invalidMentee));
  }

  @Test
  void testUpdateInvalidMenteeThrowsException() {
    repository.create(mentee);
    var invalidMentee =
        Mentee.menteeBuilder()
            .fullName("") // Invalid: @NotBlank
            .email("invalid-email") // Invalid: @Email
            .spokenLanguages(List.of())
            .build();

    assertThrows(
        ConstraintViolationException.class, () -> repository.update(mentee.getId(), invalidMentee));

    repository.deleteById(mentee.getId());
    memberRepository.deleteById(mentee.getId());
  }
}
