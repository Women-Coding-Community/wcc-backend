package com.wcc.platform.service;

import com.wcc.platform.domain.auth.UserAccount;
import com.wcc.platform.domain.auth.UserToken;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.member.MemberDto;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.UserAccountRepository;
import com.wcc.platform.repository.UserTokenRepository;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service class for handling authentication-related operations. Provides methods for user
 * authentication, token management, and member retrieval.
 */
@Service
public class AuthService {

  private static final SecureRandom RANDOM = new SecureRandom();
  private final UserAccountRepository userAccountRepository;
  private final UserTokenRepository userTokenRepository;
  private final MemberRepository memberRepository;
  private final int tokenTtlMinutes;
  private final PasswordEncoder passwordEncoder;

  /**
   * Constructor for the AuthService class.
   *
   * @param userAccountRepository the repository for managing user account entities
   * @param userTokenRepository the repository for managing user token entities
   * @param memberRepository the repository for managing member entities
   * @param tokenTtlMinutes the time-to-live value for tokens, in minutes
   */
  public AuthService(
      final UserAccountRepository userAccountRepository,
      final UserTokenRepository userTokenRepository,
      final MemberRepository memberRepository,
      final @Value("${security.token.ttl-minutes}") int tokenTtlMinutes,
      final PasswordEncoder passwordEncoder) {
    this.userAccountRepository = userAccountRepository;
    this.userTokenRepository = userTokenRepository;
    this.memberRepository = memberRepository;
    this.tokenTtlMinutes = tokenTtlMinutes;
    this.passwordEncoder = passwordEncoder;
  }

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
}
