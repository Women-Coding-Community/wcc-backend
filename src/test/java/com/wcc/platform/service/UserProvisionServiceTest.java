package com.wcc.platform.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.auth.UserAccount;
import com.wcc.platform.domain.platform.type.RoleType;
import com.wcc.platform.repository.UserAccountRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserProvisionServiceTest {

  @Mock private UserAccountRepository userAccountRepository;

  private UserProvisionService userProvisionService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    userProvisionService = new UserProvisionService(userAccountRepository);
  }

  @Test
  void existingUserWithoutRoleProvisionRole() {
    // existing user has VIEWER role only
    final UserAccount existing = new UserAccount(42L, "alice@example.com", RoleType.VIEWER);
    when(userAccountRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(existing));

    userProvisionService.provisionUserRole(42L, "alice@example.com", RoleType.MENTOR);

    assertThat(existing.getRoles()).contains(RoleType.MENTOR);
    verify(userAccountRepository, never()).create(any());
  }

  @Test
  void existingUserWithRoleProvisionRoleNoDuplicateOrCreate() {
    final var existing = new UserAccount(43L, "bob@example.com", RoleType.MENTOR);
    when(userAccountRepository.findByEmail("bob@example.com")).thenReturn(Optional.of(existing));

    userProvisionService.provisionUserRole(43L, "bob@example.com", RoleType.MENTOR);

    assertThat(existing.getRoles()).containsExactly(RoleType.MENTOR);
    verify(userAccountRepository, never()).create(any());
  }

  @Test
  void noExistingUserProvisionRoleCreatesUserWithRole() {
    when(userAccountRepository.findByEmail("carol@example.com")).thenReturn(Optional.empty());

    userProvisionService.provisionUserRole(44L, "carol@example.com", RoleType.MENTEE);

    ArgumentCaptor<UserAccount> captor = ArgumentCaptor.forClass(UserAccount.class);
    verify(userAccountRepository).create(captor.capture());

    UserAccount created = captor.getValue();
    assertEquals(44L, created.getMemberId());
    assertEquals("carol@example.com", created.getEmail());
    assertThat(created.getRoles()).contains(RoleType.MENTEE);
  }
}
