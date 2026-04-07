package com.wcc.platform.service;

import static com.wcc.platform.factories.SetupUserAccountFactories.createAdminUserTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.auth.UserAccount;
import com.wcc.platform.domain.exceptions.ForbiddenException;
import com.wcc.platform.domain.exceptions.MemberNotFoundException;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.type.RoleType;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.UserAccountRepository;
import com.wcc.platform.repository.UserTokenRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

class AuthServiceUpdateUserRolesTest {

  @Mock private UserAccountRepository userAccountRepository;
  @Mock private UserTokenRepository userTokenRepository;
  @Mock private MemberRepository memberRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private SecurityContext securityContext;
  @Mock private Authentication authentication;

  private AuthService authService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    authService =
        new AuthService(
            userAccountRepository, userTokenRepository, memberRepository, passwordEncoder);
    ReflectionTestUtils.setField(authService, "tokenTtlMinutes", 60);
  }

  private void setCallerInContext(final UserAccount.User caller) {
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getPrincipal()).thenReturn(caller);
  }

  @Test
  @DisplayName(
      "Given a valid user ID and roles, when updating roles, then return the updated account")
  void shouldUpdateUserRolesWhenUserExists() {
    var userId = 2;
    var roles = List.of(RoleType.MENTOR, RoleType.LEADER);
    var existing = new UserAccount(userId, null, "user@wcc.dev", "hash", List.of(RoleType.VIEWER), true);
    var callerUser = new UserAccount.User(createAdminUserTest(), Member.builder().id(1L).fullName("Admin").build());

    setCallerInContext(callerUser);
    when(userAccountRepository.findById(userId)).thenReturn(Optional.of(existing));

    var result = authService.updateUserRoles(userId, roles);

    assertThat(result.getRoles()).containsExactlyInAnyOrder(RoleType.MENTOR, RoleType.LEADER);
    verify(userAccountRepository).updateRole(userId, roles);
  }

  @Test
  @DisplayName(
      "Given a user ID that does not exist, when updating roles, then throw MemberNotFoundException")
  void shouldThrowMemberNotFoundExceptionWhenUserDoesNotExist() {
    var userId = 999;

    when(userAccountRepository.findById(userId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> authService.updateUserRoles(userId, List.of(RoleType.MENTOR)))
        .isInstanceOf(MemberNotFoundException.class)
        .hasMessageContaining(String.valueOf(userId));
  }

  @Test
  @DisplayName(
      "Given caller is the same user as the target, when updating roles, then throw ForbiddenException")
  void shouldForbidUserFromModifyingOwnRoles() {
    var userId = 1;
    var selfAccount = new UserAccount(userId, 1L, "admin@wcc.dev", "hash", List.of(RoleType.ADMIN), true);
    var callerUser = new UserAccount.User(selfAccount, Member.builder().id(1L).fullName("Admin").build());

    setCallerInContext(callerUser);
    when(userAccountRepository.findById(userId)).thenReturn(Optional.of(selfAccount));

    assertThatThrownBy(() -> authService.updateUserRoles(userId, List.of(RoleType.VIEWER)))
        .isInstanceOf(ForbiddenException.class)
        .hasMessageContaining("own roles");
  }

  @Test
  @DisplayName(
      "Given a LEADER caller and a target user with ADMIN role, when updating roles, then throw ForbiddenException")
  void shouldForbidLeaderFromModifyingAdminAccount() {
    var userId = 2;
    var leaderAccount = new UserAccount(1, 1L, "leader@wcc.dev", "hash", List.of(RoleType.LEADER), true);
    var adminTarget = new UserAccount(userId, 2L, "admin@wcc.dev", "hash", List.of(RoleType.ADMIN), true);
    var callerUser = new UserAccount.User(leaderAccount, Member.builder().id(1L).fullName("Leader").build());

    setCallerInContext(callerUser);
    when(userAccountRepository.findById(userId)).thenReturn(Optional.of(adminTarget));

    assertThatThrownBy(() -> authService.updateUserRoles(userId, List.of(RoleType.VIEWER)))
        .isInstanceOf(ForbiddenException.class)
        .hasMessageContaining("ADMIN");
  }

  @Test
  @DisplayName(
      "Given a LEADER caller and roles containing ADMIN, when updating roles, then throw ForbiddenException")
  void shouldForbidLeaderFromAssigningAdminRole() {
    var userId = 2;
    var leaderAccount = new UserAccount(1, 1L, "leader@wcc.dev", "hash", List.of(RoleType.LEADER), true);
    var targetAccount = new UserAccount(userId, 2L, "user@wcc.dev", "hash", List.of(RoleType.VIEWER), true);
    var callerUser = new UserAccount.User(leaderAccount, Member.builder().id(1L).fullName("Leader").build());

    setCallerInContext(callerUser);
    when(userAccountRepository.findById(userId)).thenReturn(Optional.of(targetAccount));

    assertThatThrownBy(() -> authService.updateUserRoles(userId, List.of(RoleType.ADMIN)))
        .isInstanceOf(ForbiddenException.class)
        .hasMessageContaining("ADMIN");
  }
}
