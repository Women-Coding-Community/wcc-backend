package com.wcc.platform.service;

import static com.wcc.platform.domain.auth.Permission.MENTEE_APPROVE;
import static com.wcc.platform.domain.auth.Permission.MENTOR_APPROVE;
import static com.wcc.platform.factories.SetupUserAccountFactories.createAdminUserTest;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthServicePermissionsTest {

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

  @Test
  void testRequireAnyPermissionUserHasPermissionSucceeds() {
    Integer userId = 1;
    Long memberId = 1L;

    UserAccount userAccount = createAdminUserTest();

    Member member = Member.builder().id(memberId).fullName("John Doe").build();
    UserAccount.User user = new UserAccount.User(userAccount, member);

    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getPrincipal()).thenReturn(user);

    authService.requireAnyPermission(MENTOR_APPROVE);

    assertDoesNotThrow(() -> authService.requireAnyPermission(MENTOR_APPROVE));
  }

  @Test
  void testRequireAnyPermissionUserLacksPermissionThrowsForbiddenException() {
    Integer userId = 1;
    Long memberId = 1L;

    UserAccount userAccount =
        new UserAccount(
            userId, memberId, "user@example.com", "hash", List.of(RoleType.VIEWER), false);

    Member member = Member.builder().id(memberId).fullName("John Doe").build();
    UserAccount.User user = new UserAccount.User(userAccount, member);

    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getPrincipal()).thenReturn(user);

    assertThrows(ForbiddenException.class, () -> authService.requireAnyPermission(MENTOR_APPROVE));
  }

  @Test
  void testRequireAllPermissionsUserHasAllPermissionsSucceeds() {
    Integer userId = 1;
    Long memberId = 1L;

    UserAccount userAccount = createAdminUserTest();

    Member member = Member.builder().id(memberId).fullName("John Doe").build();
    UserAccount.User user = new UserAccount.User(userAccount, member);

    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getPrincipal()).thenReturn(user);

    authService.requireAllPermissions(MENTOR_APPROVE, MENTEE_APPROVE);

    assertDoesNotThrow(() -> authService.requireAllPermissions(MENTOR_APPROVE, MENTEE_APPROVE));
  }

  @Test
  void testRequireAllPermissionsUserMissesOnePermissionThrowsForbiddenException() {
    Integer userId = 1;
    Long memberId = 1L;

    UserAccount userAccount =
        new UserAccount(
            userId, memberId, "user@example.com", "hash", List.of(RoleType.VIEWER), false);

    Member member = Member.builder().id(memberId).fullName("John Doe").build();
    UserAccount.User user = new UserAccount.User(userAccount, member);

    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getPrincipal()).thenReturn(user);

    assertThrows(
        ForbiddenException.class,
        () -> authService.requireAllPermissions(MENTOR_APPROVE, MENTEE_APPROVE));
  }

  @Test
  void testRequireRoleUserHasRequiredRoleSucceeds() {
    Integer userId = 1;
    Long memberId = 1L;

    UserAccount userAccount = createAdminUserTest();

    Member member = Member.builder().id(memberId).fullName("John Doe").build();
    UserAccount.User user = new UserAccount.User(userAccount, member);

    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getPrincipal()).thenReturn(user);

    authService.requireRole(RoleType.ADMIN);
  }

  @Test
  void testRequireRoleUserLacksRequiredRoleThrowsForbiddenException() {
    Integer userId = 1;
    Long memberId = 1L;

    UserAccount userAccount =
        new UserAccount(
            userId, memberId, "user@example.com", "hash", List.of(RoleType.VIEWER), false);

    Member member = Member.builder().id(memberId).fullName("John Doe").build();
    UserAccount.User user = new UserAccount.User(userAccount, member);

    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getPrincipal()).thenReturn(user);

    assertThrows(ForbiddenException.class, () -> authService.requireRole(RoleType.ADMIN));
  }

  @Test
  void testRequireRoleUserHasAnyOfMultipleRolesSucceeds() {
    Integer userId = 1;
    Long memberId = 1L;

    UserAccount userAccount =
        new UserAccount(
            userId, memberId, "user@example.com", "hash", List.of(RoleType.MENTOR), false);

    Member member = Member.builder().id(memberId).fullName("John Doe").build();
    UserAccount.User user = new UserAccount.User(userAccount, member);

    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getPrincipal()).thenReturn(user);

    authService.requireRole(RoleType.ADMIN, RoleType.MENTOR);
  }
}
