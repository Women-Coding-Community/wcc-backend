package com.wcc.platform.repository.postgres;

import com.wcc.platform.configuration.CacheConfig;
import com.wcc.platform.domain.auth.UserToken;
import com.wcc.platform.repository.UserTokenRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

/**
 * Caching decorator for {@link UserTokenRepository} that sits in front of {@link
 * PostgresUserTokenRepository}. Postgres remains the source of truth; this class keeps an
 * in-process Caffeine cache (keyed by token value) consistent with it so that most token
 * validations on the request hot path (every call through {@code TokenAuthFilter}) avoid a database
 * round-trip.
 *
 * <p>Every write path that changes a token's validity (create, revoke, revoke-all) updates or
 * evicts the corresponding cache entries in the same call, so a revoked or replaced token can never
 * be served as valid from a stale cache entry.
 */
@Primary
@Repository
public class CachingUserTokenRepository implements UserTokenRepository {

  private final PostgresUserTokenRepository delegate;
  private final Cache cache;

  public CachingUserTokenRepository(
      final PostgresUserTokenRepository delegate, final CacheManager cacheManager) {
    this.delegate = delegate;
    this.cache = cacheManager.getCache(CacheConfig.USER_TOKENS_CACHE);
  }

  @Override
  public UserToken create(final UserToken token) {
    final UserToken created = delegate.create(token);
    cache.put(created.getToken(), created);
    return created;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Serves from cache when possible, but always re-checks {@code expiresAt} against {@code now}.
   */
  @Override
  public Optional<UserToken> findValidByToken(final String token, final OffsetDateTime now) {
    final UserToken cached = cache.get(token, UserToken.class);
    if (cached != null && !cached.isRevoked() && cached.getExpiresAt().isAfter(now)) {
      return Optional.of(cached);
    }

    final Optional<UserToken> fromDb = delegate.findValidByToken(token, now);
    if (fromDb.isPresent()) {
      cache.put(token, fromDb.get());
    } else {
      cache.evictIfPresent(token);
    }
    return fromDb;
  }

  @Override
  public void revoke(final String token) {
    delegate.revoke(token);
    cache.evictIfPresent(token);
  }

  @Override
  public void purgeExpired(final OffsetDateTime now) {
    delegate.purgeExpired(now);
  }

  @Override
  public List<String> revokeAllForUser(final Integer userId) {
    final List<String> revokedTokens = delegate.revokeAllForUser(userId);
    revokedTokens.forEach(cache::evictIfPresent);
    return revokedTokens;
  }

  @Override
  public void lockUser(final Integer userId) {
    delegate.lockUser(userId);
  }
}
