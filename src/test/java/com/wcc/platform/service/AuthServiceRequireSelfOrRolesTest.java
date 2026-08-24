package com.wcc.platform.service;

import static com.wcc.platform.factories.SetupUserAccountFactories.createAdminUserTest;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.auth.UserAccount;
import com.wcc.platform.domain.exceptions.ForbiddenException;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.type.RoleType;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.UserAccountRepository;
import com.wcc.platform.repository.UserTokenRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthServiceRequireSelfOrRolesTest {

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
  }

  private void setupSecurityContext(final UserAccount.User user) {
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getPrincipal()).thenReturn(user);
  }

  @Test
  @DisplayName(
      "Given user has ADMIN role, when requireSelfOrRoles called with a different memberId,"
          + " then access is granted")
  void shouldGrantAccessWhenUserHasAdminRole() {
    final Long resourceMemberId = 99L;
    final UserAccount account = createAdminUserTest();
    setupSecurityContext(new UserAccount.User(account, Member.builder().id(1L).build()));

    assertDoesNotThrow(
        () -> authService.requireSelfOrRoles(resourceMemberId, RoleType.ADMIN, RoleType.MENTORSHIP_ADMIN));
  }

  @Test
  @DisplayName(
      "Given user has MENTORSHIP_ADMIN role, when requireSelfOrRoles called with a different"
          + " memberId, then access is granted")
  void shouldGrantAccessWhenUserHasMentorshipAdminRole() {
    final Long resourceMemberId = 99L;
    final UserAccount account =
        new UserAccount(1, 2L, "admin@wcc.dev", "hash", List.of(RoleType.MENTORSHIP_ADMIN), true);
    setupSecurityContext(new UserAccount.User(account, Member.builder().id(2L).build()));

    assertDoesNotThrow(
        () -> authService.requireSelfOrRoles(resourceMemberId, RoleType.ADMIN, RoleType.MENTORSHIP_ADMIN));
  }

  @Test
  @DisplayName(
      "Given user memberId matches resource memberId, when requireSelfOrRoles called,"
          + " then access is granted")
  void shouldGrantAccessWhenUserIsOwner() {
    final Long memberId = 42L;
    final UserAccount account =
        new UserAccount(1, memberId, "mentor@wcc.dev", "hash", List.of(RoleType.MENTOR), true);
    setupSecurityContext(new UserAccount.User(account, Member.builder().id(memberId).build()));

    assertDoesNotThrow(
        () -> authService.requireSelfOrRoles(memberId, RoleType.ADMIN, RoleType.MENTORSHIP_ADMIN));
  }

  @Test
  @DisplayName(
      "Given user has LEADER role and LEADER is included in allowed roles, when requireSelfOrRoles"
          + " called with a different memberId, then access is granted")
  void shouldGrantAccessWhenUserHasLeaderRoleAndLeaderIsAllowed() {
    final Long resourceMemberId = 99L;
    final UserAccount account =
        new UserAccount(1, 2L, "leader@wcc.dev", "hash", List.of(RoleType.LEADER), true);
    setupSecurityContext(new UserAccount.User(account, Member.builder().id(2L).build()));

    assertDoesNotThrow(
        () ->
            authService.requireSelfOrRoles(
                resourceMemberId, RoleType.ADMIN, RoleType.LEADER, RoleType.MENTORSHIP_ADMIN));
  }

  @Test
  @DisplayName(
      "Given user has no matching role and a different memberId, when requireSelfOrRoles called,"
          + " then ForbiddenException is thrown")
  void shouldThrowForbiddenWhenUserIsNeitherOwnerNorAdmin() {
    final Long resourceMemberId = 99L;
    final Long userMemberId = 42L;
    final UserAccount account =
        new UserAccount(1, userMemberId, "mentor@wcc.dev", "hash", List.of(RoleType.MENTOR), true);
    setupSecurityContext(new UserAccount.User(account, Member.builder().id(userMemberId).build()));

    assertThatThrownBy(
            () ->
                authService.requireSelfOrRoles(
                    resourceMemberId, RoleType.ADMIN, RoleType.MENTORSHIP_ADMIN))
        .isInstanceOf(ForbiddenException.class);
  }

  @Test
  @DisplayName(
      "Given user has LEADER role but LEADER is not in allowed roles, when requireSelfOrRoles"
          + " called with a different memberId, then ForbiddenException is thrown")
  void shouldThrowForbiddenWhenLeaderNotInAllowedRoles() {
    final Long resourceMemberId = 99L;
    final UserAccount account =
        new UserAccount(1, 2L, "leader@wcc.dev", "hash", List.of(RoleType.LEADER), true);
    setupSecurityContext(new UserAccount.User(account, Member.builder().id(2L).build()));

    assertThatThrownBy(
            () ->
                authService.requireSelfOrRoles(
                    resourceMemberId, RoleType.ADMIN, RoleType.MENTORSHIP_ADMIN))
        .isInstanceOf(ForbiddenException.class);
  }
}
