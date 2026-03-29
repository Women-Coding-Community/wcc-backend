package com.wcc.platform.repository;

import com.wcc.platform.domain.auth.UserToken;
import java.time.OffsetDateTime;
import java.util.Optional;

public interface UserTokenRepository {
  UserToken create(UserToken token);

  Optional<UserToken> findValidByToken(String token, OffsetDateTime now);

  void revoke(String token);

  void purgeExpired(OffsetDateTime now);

  /**
   * Revokes all active session tokens for the given user.
   *
   * @param userId the user account ID whose tokens should be revoked
   */
  void revokeAllForUser(Integer userId);
}
