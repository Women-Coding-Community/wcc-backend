package com.wcc.platform.service;

import static com.wcc.platform.factories.SetupUserAccountFactories.createAdminUserTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.auth.Permission;
import com.wcc.platform.domain.auth.UserAccount;
import com.wcc.platform.domain.auth.UserToken;
import com.wcc.platform.domain.exceptions.ForbiddenException;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.type.MemberType;
import com.wcc.platform.domain.platform.type.RoleType;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.UserAccountRepository;
import com.wcc.platform.repository.UserTokenRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

class AuthServiceTest {

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

  // ==================== findUserByEmail Tests ====================

  @Test
  void testFindUserByEmail_userExists_returnsUserAccount() {
    var email = "user@example.com";
    UserAccount userAccount = createAdminUserTest();
    when(userAccountRepository.findByEmail(email)).thenReturn(Optional.of(userAccount));

    Optional<UserAccount> result = authService.findUserByEmail(email);

    assertTrue(result.isPresent());
    assertEquals(email, result.get().getEmail());
    verify(userAccountRepository).findByEmail(email);
  }

  @Test
  void testFindUserByEmail_userNotFound_returnsEmpty() {
    var email = "notfound@example.com";
    when(userAccountRepository.findByEmail(email)).thenReturn(Optional.empty());

    Optional<UserAccount> result = authService.findUserByEmail(email);

    assertTrue(result.isEmpty());
  }

  // ==================== getMember Tests ====================

  @Test
  void testGetMember_memberExists_returnsMemberDto() {
    Long memberId = 1L;
    Member member =
        Member.builder().id(memberId).fullName("John Doe").email("john@example.com").build();

    when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

    var result = authService.getMember(memberId);

    assertNotNull(result);
    assertEquals("John Doe", result.getFullName());
  }

  @Test
  void testGetMember_memberNotFound_returnsNull() {
    Long memberId = 999L;
    when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

    var result = authService.getMember(memberId);

    assertNull(result);
  }

  @Test
  void testGetMember_nullId_returnsNull() {
    var result = authService.getMember(null);

    assertNull(result);
    verify(memberRepository, never()).findById(any());
  }

  // ==================== authenticateAndIssueToken Tests ====================

  @Test
  void testAuthenticateAndIssueToken_validCredentials_returnsToken() {
    String email = "user@example.com";
    String password = "password123";
    String passwordHash = "hashed_password";

    UserAccount userAccount =
        new UserAccount(1, 1L, email, passwordHash, List.of(RoleType.ADMIN), true);

    when(userAccountRepository.findByEmail(email)).thenReturn(Optional.of(userAccount));
    when(passwordEncoder.matches(password, passwordHash)).thenReturn(true);
    when(userTokenRepository.create(any(UserToken.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Optional<UserToken> result = authService.authenticateAndIssueToken(email, password);

    assertTrue(result.isPresent());
    assertEquals(1, result.get().getUserId());
    assertFalse(result.get().isRevoked());
  }

  @Test
  void testAuthenticateAndIssueToken_userNotFound_returnsEmpty() {
    String email = "notfound@example.com";
    String password = "password123";

    when(userAccountRepository.findByEmail(email)).thenReturn(Optional.empty());

    Optional<UserToken> result = authService.authenticateAndIssueToken(email, password);

    assertTrue(result.isEmpty());
  }

  @Test
  void testAuthenticateAndIssueToken_userDisabled_returnsEmpty() {
    String email = "user@example.com";
    String password = "password123";

    UserAccount userAccount = new UserAccount(1, 1L, email, "hash", List.of(RoleType.ADMIN), false);

    when(userAccountRepository.findByEmail(email)).thenReturn(Optional.of(userAccount));

    Optional<UserToken> result = authService.authenticateAndIssueToken(email, password);

    assertTrue(result.isEmpty());
  }

  @Test
  void testAuthenticateAndIssueToken_invalidPassword_returnsEmpty() {
    String email = "user@example.com";
    String password = "wrongpassword";
    String passwordHash = "hashed_password";

    UserAccount userAccount =
        new UserAccount(1, 1L, email, passwordHash, List.of(RoleType.ADMIN), true);

    when(userAccountRepository.findByEmail(email)).thenReturn(Optional.of(userAccount));
    when(passwordEncoder.matches(password, passwordHash)).thenReturn(false);

    Optional<UserToken> result = authService.authenticateAndIssueToken(email, password);

    assertTrue(result.isEmpty());
  }

  // ==================== authenticateByToken Tests ====================

  @Test
  void testAuthenticateByToken_validToken_returnsUserAccount() {
    String token = "valid-token";
    Integer userId = 1;

    UserToken userToken =
        UserToken.builder()
            .token(token)
            .userId(userId)
            .issuedAt(OffsetDateTime.now())
            .expiresAt(OffsetDateTime.now().plusHours(1))
            .revoked(false)
            .build();

    UserAccount userAccount = createAdminUserTest();

    when(userTokenRepository.findValidByToken(eq(token), any(OffsetDateTime.class)))
        .thenReturn(Optional.of(userToken));
    when(userAccountRepository.findById(userId)).thenReturn(Optional.of(userAccount));

    Optional<UserAccount> result = authService.authenticateByToken(token);

    assertTrue(result.isPresent());
    assertEquals("user@example.com", result.get().getEmail());
  }

  @Test
  void testAuthenticateByToken_invalidToken_returnsEmpty() {
    String token = "invalid-token";

    when(userTokenRepository.findValidByToken(eq(token), any(OffsetDateTime.class)))
        .thenReturn(Optional.empty());

    Optional<UserAccount> result = authService.authenticateByToken(token);

    assertTrue(result.isEmpty());
  }

  @Test
  void testAuthenticateByToken_userNotFound_returnsEmpty() {
    String token = "valid-token";
    Integer userId = 999;

    UserToken userToken =
        UserToken.builder()
            .token(token)
            .userId(userId)
            .issuedAt(OffsetDateTime.now())
            .expiresAt(OffsetDateTime.now().plusHours(1))
            .revoked(false)
            .build();

    when(userTokenRepository.findValidByToken(eq(token), any(OffsetDateTime.class)))
        .thenReturn(Optional.of(userToken));
    when(userAccountRepository.findById(userId)).thenReturn(Optional.empty());

    Optional<UserAccount> result = authService.authenticateByToken(token);

    assertTrue(result.isEmpty());
  }

  // ==================== getUserWithMember Tests ====================

  @Test
  void testGetUserWithMember_userAndMemberExist_returnsUser() {
    Integer userId = 1;
    Long memberId = 1L;

    UserAccount userAccount = createAdminUserTest();

    Member member =
        Member.builder()
            .id(memberId)
            .fullName("John Doe")
            .memberTypes(List.of(MemberType.LEADER))
            .build();

    when(userAccountRepository.findById(userId)).thenReturn(Optional.of(userAccount));
    when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

    Optional<UserAccount.User> result = authService.getUserWithMember(userId);

    assertTrue(result.isPresent());
    assertEquals("user@example.com", result.get().userAccount().getEmail());
    assertEquals("John Doe", result.get().member().getFullName());
  }

  @Test
  void testGetUserWithMember_userHasNoMemberId_returnsEmpty() {
    Integer userId = 1;

    UserAccount userAccount =
        new UserAccount(userId, null, "user@example.com", "hash", List.of(RoleType.ADMIN), true);

    when(userAccountRepository.findById(userId)).thenReturn(Optional.of(userAccount));

    when(userAccountRepository.findById(userId)).thenReturn(Optional.of(userAccount));

    Optional<UserAccount.User> result = authService.getUserWithMember(userId);

    assertTrue(result.isEmpty());
  }

  // ==================== authenticateByTokenWithMember Tests ====================

  @Test
  void testAuthenticateByTokenWithMember_validToken_returnsUserWithMember() {
    String token = "valid-token";
    Integer userId = 1;
    Long memberId = 1L;

    UserToken userToken =
        UserToken.builder()
            .token(token)
            .userId(userId)
            .issuedAt(OffsetDateTime.now())
            .expiresAt(OffsetDateTime.now().plusHours(1))
            .revoked(false)
            .build();

    UserAccount userAccount = createAdminUserTest();

    Member member = Member.builder().id(memberId).fullName("John Doe").build();

    when(userTokenRepository.findValidByToken(eq(token), any())).thenReturn(Optional.of(userToken));
    when(userAccountRepository.findById(userId)).thenReturn(Optional.of(userAccount));
    when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

    Optional<UserAccount.User> result = authService.authenticateByTokenWithMember(token);

    assertTrue(result.isPresent());
    assertEquals("user@example.com", result.get().userAccount().getEmail());
  }

  @Test
  void testAuthenticateByTokenWithMember_invalidToken_returnsEmpty() {
    var token = "invalid-token";

    when(userTokenRepository.findValidByToken(eq(token), any(OffsetDateTime.class)))
        .thenReturn(Optional.empty());

    Optional<UserAccount.User> result = authService.authenticateByTokenWithMember(token);

    assertTrue(result.isEmpty());
  }

  // ==================== getCurrentUser Tests ====================

  @Test
  void testGetCurrentUser_validAuthentication_returnsUser() {
    var userId = 1;
    var memberId = 1L;

    UserAccount userAccount = createAdminUserTest();

    Member member = Member.builder().id(memberId).fullName("John Doe").build();
    UserAccount.User user = new UserAccount.User(userAccount, member);

    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getPrincipal()).thenReturn(user);

    UserAccount.User result = authService.getCurrentUser();

    assertEquals("user@example.com", result.userAccount().getEmail());
  }

  @Test
  void testGetCurrentUser_notAuthenticated_throwsForbiddenException() {
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(null);

    assertThrows(ForbiddenException.class, () -> authService.getCurrentUser());
  }

  @Test
  void testGetCurrentUser_invalidPrincipal_throwsForbiddenException() {
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getPrincipal()).thenReturn("invalid-principal");

    assertThrows(ForbiddenException.class, () -> authService.getCurrentUser());
  }

  // ==================== requireAnyPermission Tests ====================

  @Test
  void testRequireAnyPermission_userHasPermission_succeeds() {
    Integer userId = 1;
    Long memberId = 1L;

    UserAccount userAccount = createAdminUserTest();

    Member member = Member.builder().id(memberId).fullName("John Doe").build();
    UserAccount.User user = new UserAccount.User(userAccount, member);

    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getPrincipal()).thenReturn(user);

    // ADMIN role has MENTOR_APPROVE permission
    authService.requireAnyPermission(Permission.MENTOR_APPROVE);

    // Should not throw
  }

  @Test
  void testRequireAnyPermission_userLacksPermission_throwsForbiddenException() {
    Integer userId = 1;
    Long memberId = 1L;

    UserAccount userAccount =
        new UserAccount(
            userId, memberId, "user@example.com", "passwordHash", List.of(RoleType.VIEWER), true);

    Member member = Member.builder().id(memberId).fullName("John Doe").build();
    UserAccount.User user = new UserAccount.User(userAccount, member);

    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getPrincipal()).thenReturn(user);

    assertThrows(
        ForbiddenException.class,
        () -> authService.requireAnyPermission(Permission.MENTOR_APPROVE));
  }

  // ==================== requireAllPermissions Tests ====================

  @Test
  void testRequireAllPermissions_userHasAllPermissions_succeeds() {
    Integer userId = 1;
    Long memberId = 1L;

    UserAccount userAccount = createAdminUserTest();

    Member member = Member.builder().id(memberId).fullName("John Doe").build();
    UserAccount.User user = new UserAccount.User(userAccount, member);

    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getPrincipal()).thenReturn(user);

    // ADMIN has both permissions
    authService.requireAllPermissions(Permission.MENTOR_APPROVE, Permission.MENTEE_APPROVE);

    // Should not throw
  }

  @Test
  void testRequireAllPermissions_userMissesOnePermission_throwsForbiddenException() {
    Integer userId = 1;
    Long memberId = 1L;

    UserAccount userAccount =
        new UserAccount(
            userId, memberId, "user@example.com", "passwordHash", List.of(RoleType.VIEWER), true);

    Member member = Member.builder().id(memberId).fullName("John Doe").build();
    UserAccount.User user = new UserAccount.User(userAccount, member);

    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getPrincipal()).thenReturn(user);

    assertThrows(
        ForbiddenException.class,
        () ->
            authService.requireAllPermissions(
                Permission.MENTOR_APPROVE, Permission.MENTEE_APPROVE));
  }

  // ==================== requireRole Tests ====================

  @Test
  void testRequireRole_userHasRequiredRole_succeeds() {
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
  void testRequireRole_userLacksRequiredRole_throwsForbiddenException() {
    Integer userId = 1;
    Long memberId = 1L;

    UserAccount userAccount =
        new UserAccount(
            userId, memberId, "user@example.com", "passwordHash", List.of(RoleType.VIEWER), true);

    Member member = Member.builder().id(memberId).fullName("John Doe").build();
    UserAccount.User user = new UserAccount.User(userAccount, member);

    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getPrincipal()).thenReturn(user);

    assertThrows(ForbiddenException.class, () -> authService.requireRole(RoleType.ADMIN));
  }

  @Test
  void testRequireRole_userHasAnyOfMultipleRoles_succeeds() {
    Integer userId = 1;
    Long memberId = 1L;

    UserAccount userAccount =
        new UserAccount(
            userId, memberId, "user@example.com", "passwordHash", List.of(RoleType.MENTOR), true);

    Member member = Member.builder().id(memberId).fullName("John Doe").build();
    UserAccount.User user = new UserAccount.User(userAccount, member);

    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getPrincipal()).thenReturn(user);

    authService.requireRole(RoleType.ADMIN, RoleType.MENTOR);
  }
}
