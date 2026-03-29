package com.wcc.platform.repository;

import com.wcc.platform.domain.auth.PasswordResetToken;
import java.time.OffsetDateTime;
import java.util.Optional;

/** Repository for managing single-use password reset tokens. */
public interface PasswordResetTokenRepository {

  /**
   * Persists a new password reset token.
   *
   * @param token the token to store
   * @return the stored token
   */
  PasswordResetToken create(PasswordResetToken token);

  /**
   * Finds a token that is valid: not expired and not already used.
   *
   * @param token the raw token string
   * @param now the current time used for expiry comparison
   * @return the token if valid, or empty
   */
  Optional<PasswordResetToken> findValidByToken(String token, OffsetDateTime now);

  /**
   * Marks a token as used so it cannot be reused.
   *
   * @param token the raw token string to mark as used
   */
  void markUsed(String token);

  /**
   * Deletes all tokens whose expiry time is before the given time.
   *
   * @param now the cutoff time
   */
  void purgeExpired(OffsetDateTime now);
}
