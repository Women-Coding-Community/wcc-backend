package com.wcc.platform.service;

import com.wcc.platform.domain.auth.Permission;
import com.wcc.platform.domain.auth.UserAccount;
import com.wcc.platform.domain.auth.UserToken;
import com.wcc.platform.domain.exceptions.ForbiddenException;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.member.MemberDto;
import com.wcc.platform.domain.platform.type.RoleType;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.UserAccountRepository;
import com.wcc.platform.repository.UserTokenRepository;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service class for handling authentication-related operations. Provides methods for user
 * authentication, token management, and member retrieval.
 */
@Service
@RequiredArgsConstructor
@SuppressWarnings("PMD.TooManyMethods")
public class AuthService {

  private static final SecureRandom RANDOM = new SecureRandom();
  private final UserAccountRepository userAccountRepository;
  private final UserTokenRepository userTokenRepository;
  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${security.token.ttl-minutes}")
  private int tokenTtlMinutes;

  public Optional<UserAccount> findUserByEmail(final String email) {
    return userAccountRepository.findByEmail(email);
  }

  /**
   * Retrieves a member's details based on the given member ID.
   *
   * @param memberId the ID of the member to retrieve; must not be null
   * @return a {@code MemberDto} representing the member's details if found, or {@code null} if no
   *     member is found for the provided ID or if {@code memberId} is null
   */
  public MemberDto getMember(final Long memberId) {
    if (memberId == null) {
      return null;
    }

    return memberRepository.findById(memberId).map(Member::toDto).orElse(null);
  }

  /**
   * Authenticates a user based on the provided email and password and issues a token if successful.
   * The token includes information such as issuance time and expiration time.
   *
   * @param email the email address of the user attempting to authenticate
   * @param password the plaintext password of the user attempting to authenticate
   * @return an {@code Optional<UserToken>} containing the issued token if authentication is
   *     successful, or an empty {@code Optional} if authentication fails
   */
  public Optional<UserToken> authenticateAndIssueToken(final String email, final String password) {
    final Optional<UserAccount> userOpt = userAccountRepository.findByEmail(email);
    if (userOpt.isEmpty()) {
      return Optional.empty();
    }
    final UserAccount user = userOpt.get();
    if (!user.isEnabled()) {
      return Optional.empty();
    }
    if (!passwordEncoder.matches(password, user.getPasswordHash())) {
      return Optional.empty();
    }

    return Optional.of(generateUserToken(user));
  }

  /**
   * Authenticates a user based on the provided token. Verifies the validity of the token and
   * retrieves the associated user account if the token is valid and not revoked.
   *
   * @param token the authentication token provided by the user
   * @return an {@code Optional<UserAccount>} containing the user account if the token is valid and
   *     associated with an existing user, or an empty {@code Optional} if the token is invalid or
   *     no user account is found
   */
  public Optional<UserAccount> authenticateByToken(final String token) {
    final Optional<UserToken> tokenOpt =
        userTokenRepository.findValidByToken(token, OffsetDateTime.now());
    if (tokenOpt.isEmpty()) {
      return Optional.empty();
    }
    final UserToken retrievedToken = tokenOpt.get();
    return userAccountRepository.findById(retrievedToken.getUserId());
  }

  private String generateToken() {
    final byte[] bytes = new byte[48];
    RANDOM.nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }

  private UserToken generateUserToken(final UserAccount user) {
    final String token = generateToken();
    final OffsetDateTime now = OffsetDateTime.now();
    final OffsetDateTime expires = now.plusMinutes(tokenTtlMinutes);
    final UserToken userToken =
        UserToken.builder()
            .token(token)
            .userId(user.getId())
            .issuedAt(now)
            .expiresAt(expires)
            .revoked(false)
            .build();
    userTokenRepository.create(userToken);
    return userToken;
  }

  /**
   * Retrieves the complete User (UserAccount + Member) for a given user account ID. This is used
   * for RBAC permission checking.
   *
   * @param userId the ID of the user account
   * @return an {@code Optional<UserAccount.User>} containing the user and member if found
   */
  public Optional<UserAccount.User> getUserWithMember(final Integer userId) {
    if (userId == null) {
      return Optional.empty();
    }

    final Optional<UserAccount> userAccountOpt = userAccountRepository.findById(userId);
    if (userAccountOpt.isEmpty()) {
      return Optional.empty();
    }

    final UserAccount userAccount = userAccountOpt.get();
    if (userAccount.getMemberId() == null) {
      return Optional.empty();
    }

    final Optional<Member> memberOpt = memberRepository.findById(userAccount.getMemberId());
    return memberOpt.map(member -> new UserAccount.User(userAccount, member));
  }

  /**
   * Authenticates a user based on the provided token and returns the complete User (UserAccount +
   * Member). This is the preferred method for RBAC-enabled authentication.
   *
   * @param token the authentication token provided by the user
   * @return an {@code Optional<UserAccount.User>} containing the user and member if the token is
   *     valid and associated with an existing user, or an empty {@code Optional} if the token is
   *     invalid or no user account is found
   */
  public Optional<UserAccount.User> authenticateByTokenWithMember(final String token) {
    final Optional<UserToken> tokenOpt =
        userTokenRepository.findValidByToken(token, OffsetDateTime.now());
    if (tokenOpt.isEmpty()) {
      return Optional.empty();
    }

    final UserToken retrievedToken = tokenOpt.get();
    return getUserWithMember(retrievedToken.getUserId());
  }

  /**
   * Get the current authenticated user with member information from SecurityContext.
   *
   * @return the authenticated User (UserAccount + Member)
   * @throws ForbiddenException if user is not authenticated or principal is invalid
   */
  public UserAccount.User getCurrentUser() {
    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      throw new ForbiddenException("User is not authenticated");
    }

    final Object principal = authentication.getPrincipal();

    if (principal instanceof UserAccount.User) {
      return (UserAccount.User) principal;
    }

    throw new ForbiddenException("Invalid authentication principal");
  }

  /**
   * Require any of the specified permissions (OR logic).
   *
   * @param permissions the permissions (user needs at least one)
   * @throws ForbiddenException if user doesn't have any of the permissions
   */
  public void requireAnyPermission(final Permission... permissions) {
    final UserAccount.User user = getCurrentUser();
    final Set<Permission> userPermissions = user.getAllPermissions();

    final boolean hasAny = Arrays.stream(permissions).anyMatch(userPermissions::contains);

    if (!hasAny) {
      throw new ForbiddenException(
          String.format("Permission denied. Required any of: %s", Arrays.toString(permissions)));
    }
  }

  /**
   * Require all of the specified permissions (AND logic).
   *
   * @param permissions the permissions (user needs all of them)
   * @throws ForbiddenException if user doesn't have all the permissions
   */
  public void requireAllPermissions(final Permission... permissions) {
    final UserAccount.User user = getCurrentUser();
    final Set<Permission> userPermissions = user.getAllPermissions();

    final boolean hasAll = Arrays.stream(permissions).allMatch(userPermissions::contains);

    if (!hasAll) {
      throw new ForbiddenException(
          String.format("Permission denied. Required all of: %s", Arrays.toString(permissions)));
    }
  }

  /**
   * Require specific role(s). User needs at least one of the specified roles (from member types or
   * assigned roles).
   *
   * @param allowedRoles the allowed roles (user needs one of them)
   * @throws ForbiddenException if user doesn't have any of the roles
   */
  public void requireRole(final RoleType... allowedRoles) {
    final UserAccount.User user = getCurrentUser();

    if (user.hasAnyRole(allowedRoles)) {
      return;
    }

    throw new ForbiddenException(
        String.format(
            "Role denied. User roles: %s, Required any of: %s",
            user.getAllRoles(), Arrays.toString(allowedRoles)));
  }
}
