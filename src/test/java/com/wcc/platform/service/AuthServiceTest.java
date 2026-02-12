package com.wcc.platform.service;

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
  void testFindUserByEmailUserExistsReturnsUserAccount() {
    var email = "user@example.com";
    UserAccount userAccount =
        UserAccount.builder()
            .id(1)
            .email(email)
            .memberId(1L)
            .enabled(true)
            .roles(List.of(RoleType.ADMIN))
            .build();

    when(userAccountRepository.findByEmail(email)).thenReturn(Optional.of(userAccount));

    Optional<UserAccount> result = authService.findUserByEmail(email);

    assertTrue(result.isPresent());
    assertEquals(email, result.get().getEmail());
    verify(userAccountRepository).findByEmail(email);
  }

  @Test
  void testFindUserByEmailUserNotFoundReturnsEmpty() {
    var email = "notfound@example.com";
    when(userAccountRepository.findByEmail(email)).thenReturn(Optional.empty());

    Optional<UserAccount> result = authService.findUserByEmail(email);

    assertTrue(result.isEmpty());
  }

  // ==================== getMember Tests ====================

  @Test
  void testGetMemberMemberExistsReturnsMemberDto() {
    Long memberId = 1L;
    Member member =
        Member.builder().id(memberId).fullName("John Doe").email("john@example.com").build();

    when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

    var result = authService.getMember(memberId);

    assertNotNull(result);
    assertEquals("John Doe", result.getFullName());
  }

  @Test
  void testGetMemberMemberNotFoundReturnsNull() {
    Long memberId = 999L;
    when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

    var result = authService.getMember(memberId);

    assertNull(result);
  }

  @Test
  void testGetMemberNullIdReturnsNull() {
    var result = authService.getMember(null);

    assertNull(result);
    verify(memberRepository, never()).findById(any());
  }

  // ==================== authenticateAndIssueToken Tests ====================

  @Test
  void testAuthenticateAndIssueTokenValidCredentialsReturnsToken() {
    String email = "user@example.com";
    String password = "password123";
    String passwordHash = "hashed_password";

    UserAccount userAccount =
        UserAccount.builder()
            .id(1)
            .email(email)
            .passwordHash(passwordHash)
            .enabled(true)
            .roles(List.of(RoleType.ADMIN))
            .build();

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
  void testAuthenticateAndIssueTokenUserNotFoundReturnsEmpty() {
    String email = "notfound@example.com";
    String password = "password123";

    when(userAccountRepository.findByEmail(email)).thenReturn(Optional.empty());

    Optional<UserToken> result = authService.authenticateAndIssueToken(email, password);

    assertTrue(result.isEmpty());
  }

  @Test
  void testAuthenticateAndIssueTokenUserDisabledReturnsEmpty() {
    String email = "user@example.com";
    String password = "password123";

    UserAccount userAccount =
        UserAccount.builder().id(1).email(email).passwordHash("hash").enabled(false).build();

    when(userAccountRepository.findByEmail(email)).thenReturn(Optional.of(userAccount));

    Optional<UserToken> result = authService.authenticateAndIssueToken(email, password);

    assertTrue(result.isEmpty());
  }

  @Test
  void testAuthenticateAndIssueTokenInvalidPasswordReturnsEmpty() {
    String email = "user@example.com";
    String password = "wrongpassword";
    String passwordHash = "hashed_password";

    UserAccount userAccount =
        UserAccount.builder().id(1).email(email).passwordHash(passwordHash).enabled(true).build();

    when(userAccountRepository.findByEmail(email)).thenReturn(Optional.of(userAccount));
    when(passwordEncoder.matches(password, passwordHash)).thenReturn(false);

    Optional<UserToken> result = authService.authenticateAndIssueToken(email, password);

    assertTrue(result.isEmpty());
  }

  // ==================== authenticateByToken Tests ====================

  @Test
  void testAuthenticateByTokenValidTokenReturnsUserAccount() {
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

    UserAccount userAccount =
        UserAccount.builder()
            .id(userId)
            .email("user@example.com")
            .enabled(true)
            .roles(List.of(RoleType.ADMIN))
            .build();

    when(userTokenRepository.findValidByToken(eq(token), any(OffsetDateTime.class)))
        .thenReturn(Optional.of(userToken));
    when(userAccountRepository.findById(userId)).thenReturn(Optional.of(userAccount));

    Optional<UserAccount> result = authService.authenticateByToken(token);

    assertTrue(result.isPresent());
    assertEquals("user@example.com", result.get().getEmail());
  }

  @Test
  void testAuthenticateByTokenInvalidTokenReturnsEmpty() {
    String token = "invalid-token";

    when(userTokenRepository.findValidByToken(eq(token), any(OffsetDateTime.class)))
        .thenReturn(Optional.empty());

    Optional<UserAccount> result = authService.authenticateByToken(token);

    assertTrue(result.isEmpty());
  }

  @Test
  void testAuthenticateByTokenUserNotFoundReturnsEmpty() {
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
  void testGetUserWithMemberUserAndMemberExistReturnsUser() {
    Integer userId = 1;
    Long memberId = 1L;

    UserAccount userAccount =
        UserAccount.builder()
            .id(userId)
            .memberId(memberId)
            .email("user@example.com")
            .roles(List.of(RoleType.ADMIN))
            .build();

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
  void testGetUserWithMemberUserHasNoMemberIdReturnsEmpty() {
    Integer userId = 1;

    UserAccount userAccount = UserAccount.builder().id(userId).memberId(null).build();

    when(userAccountRepository.findById(userId)).thenReturn(Optional.of(userAccount));

    Optional<UserAccount.User> result = authService.getUserWithMember(userId);

    assertTrue(result.isEmpty());
  }

  // ==================== authenticateByTokenWithMember Tests ====================

  @Test
  void testAuthenticateByTokenWithMemberValidTokenReturnsUserWithMember() {
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

    UserAccount userAccount =
        UserAccount.builder()
            .id(userId)
            .memberId(memberId)
            .email("user@example.com")
            .roles(List.of(RoleType.ADMIN))
            .build();

    Member member = Member.builder().id(memberId).fullName("John Doe").build();

    when(userTokenRepository.findValidByToken(eq(token), any())).thenReturn(Optional.of(userToken));
    when(userAccountRepository.findById(userId)).thenReturn(Optional.of(userAccount));
    when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

    Optional<UserAccount.User> result = authService.authenticateByTokenWithMember(token);

    assertTrue(result.isPresent());
    assertEquals("user@example.com", result.get().userAccount().getEmail());
  }

  @Test
  void testAuthenticateByTokenWithMemberInvalidTokenReturnsEmpty() {
    var token = "invalid-token";

    when(userTokenRepository.findValidByToken(eq(token), any(OffsetDateTime.class)))
        .thenReturn(Optional.empty());

    Optional<UserAccount.User> result = authService.authenticateByTokenWithMember(token);

    assertTrue(result.isEmpty());
  }

  // ==================== getCurrentUser Tests ====================

  @Test
  void testGetCurrentUserValidAuthenticationReturnsUser() {
    var userId = 1;
    var memberId = 1L;

    UserAccount userAccount =
        UserAccount.builder()
            .id(userId)
            .memberId(memberId)
            .email("user@example.com")
            .roles(List.of(RoleType.ADMIN))
            .build();

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
  void testGetCurrentUserNotAuthenticatedThrowsForbiddenException() {
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(null);

    assertThrows(ForbiddenException.class, authService::getCurrentUser);
  }

  @Test
  void testGetCurrentUserInvalidPrincipalThrowsForbiddenException() {
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getPrincipal()).thenReturn("invalid-principal");

    assertThrows(ForbiddenException.class, authService::getCurrentUser);
  }
}
