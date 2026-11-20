package com.wcc.platform.repository.postgres;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.auth.UserAccount;
import com.wcc.platform.domain.platform.type.RoleType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * Unit tests for {@link PostgresUserAccountRepository}, covering email case normalization so that
 * {@code create} and {@code update} always persist a lowercase email consistent with {@code
 * findByEmail}'s lowercase lookup.
 */
class PostgresUserAccountRepositoryTest {

  private JdbcTemplate jdbc;
  private PostgresUserAccountRepository repository;

  @BeforeEach
  void setUp() {
    jdbc = mock(JdbcTemplate.class);
    repository = new PostgresUserAccountRepository(jdbc);
  }

  @Test
  @DisplayName(
      "Given a mixed-case email, when creating a user account, then the lowercased email is"
          + " persisted")
  void shouldLowercaseEmailWhenCreatingUserAccount() {
    UserAccount userAccount =
        new UserAccount(null, 1L, "Admin@Example.COM", "hash", List.of(RoleType.ADMIN), true);

    stubFindByEmailReturning(mappedUserAccount("admin@example.com"));

    repository.create(userAccount);

    ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
    verify(jdbc)
        .update(
            anyString(),
            eq(1L),
            emailCaptor.capture(),
            eq("hash"));

    assertThat(emailCaptor.getValue()).isEqualTo("admin@example.com");
  }

  @Test
  @DisplayName(
      "Given a mixed-case email, when updating a user account, then the lowercased email is"
          + " persisted")
  void shouldLowercaseEmailWhenUpdatingUserAccount() {
    UserAccount userAccount =
        new UserAccount(1, 1L, "Admin@Example.COM", "hash", List.of(RoleType.ADMIN), true);

    repository.update(1, userAccount);

    ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
    verify(jdbc)
        .update(
            anyString(),
            eq(1L),
            emailCaptor.capture(),
            eq("hash"),
            eq(true),
            eq(1));

    assertThat(emailCaptor.getValue()).isEqualTo("admin@example.com");
  }

  @Test
  @DisplayName(
      "Given an account created with a mixed-case email, when finding it by its original casing,"
          + " then it is still found because the lookup lowercases the search term")
  void shouldFindCreatedAccountRegardlessOfOriginalCasing() {
    UserAccount userAccount =
        new UserAccount(null, 1L, "Admin@Example.COM", "hash", List.of(RoleType.ADMIN), true);

    stubFindByEmailReturning(mappedUserAccount("admin@example.com"));

    UserAccount result = repository.create(userAccount);

    assertThat(result.getEmail()).isEqualTo("admin@example.com");
  }

  @SuppressWarnings("unchecked")
  private void stubFindByEmailReturning(final UserAccount userAccount) {
    when(jdbc.query(anyString(), any(RowMapper.class), anyString()))
        .thenReturn(List.of(userAccount));
  }

  private UserAccount mappedUserAccount(final String email) {
    return new UserAccount(1, 1L, email, "hash", List.of(RoleType.ADMIN), true);
  }
}
