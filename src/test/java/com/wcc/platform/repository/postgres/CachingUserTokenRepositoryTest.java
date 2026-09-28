package com.wcc.platform.repository.postgres;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.configuration.CacheConfig;
import com.wcc.platform.domain.auth.UserToken;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;

class CachingUserTokenRepositoryTest {

  private PostgresUserTokenRepository delegate;
  private ConcurrentMapCache cache;
  private CachingUserTokenRepository repository;

  @BeforeEach
  void setUp() {
    delegate = mock(PostgresUserTokenRepository.class);
    cache = new ConcurrentMapCache(CacheConfig.USER_TOKENS_CACHE);

    final CacheManager cacheManager = mock(CacheManager.class);
    when(cacheManager.getCache(CacheConfig.USER_TOKENS_CACHE)).thenReturn(cache);

    repository = new CachingUserTokenRepository(delegate, cacheManager);
  }

  private UserToken buildToken(
      final String token, final Integer userId, final OffsetDateTime expiresAt) {
    return UserToken.builder()
        .token(token)
        .userId(userId)
        .issuedAt(OffsetDateTime.now())
        .expiresAt(expiresAt)
        .revoked(false)
        .build();
  }

  @Test
  void shouldPopulateCacheOnCreate() {
    final UserToken token = buildToken("tok-1", 1, OffsetDateTime.now().plusHours(1));
    when(delegate.create(token)).thenReturn(token);

    final UserToken created = repository.create(token);

    assertThat(created).isEqualTo(token);
    assertThat(cache.get("tok-1", UserToken.class)).isEqualTo(token);
  }

  @Test
  void shouldReturnCachedTokenWithoutHittingDatabaseOnCacheHit() {
    final UserToken token = buildToken("tok-1", 1, OffsetDateTime.now().plusHours(1));
    cache.put("tok-1", token);

    final Optional<UserToken> result = repository.findValidByToken("tok-1", OffsetDateTime.now());

    assertThat(result).contains(token);
    verify(delegate, never()).findValidByToken(any(), any());
  }

  @Test
  void shouldFallBackToDatabaseAndPopulateCacheOnCacheMiss() {
    final UserToken token = buildToken("tok-1", 1, OffsetDateTime.now().plusHours(1));
    when(delegate.findValidByToken(eq("tok-1"), any(OffsetDateTime.class)))
        .thenReturn(Optional.of(token));

    final Optional<UserToken> result = repository.findValidByToken("tok-1", OffsetDateTime.now());

    assertThat(result).contains(token);
    assertThat(cache.get("tok-1", UserToken.class)).isEqualTo(token);
  }

  @Test
  void shouldNotTrustCachedEntryPastItsRealExpiryEvenIfStillCached() {
    final UserToken expired = buildToken("tok-1", 1, OffsetDateTime.now().minusMinutes(1));
    cache.put("tok-1", expired);
    when(delegate.findValidByToken(eq("tok-1"), any(OffsetDateTime.class)))
        .thenReturn(Optional.empty());

    final Optional<UserToken> result = repository.findValidByToken("tok-1", OffsetDateTime.now());

    assertThat(result).isEmpty();
    verify(delegate, times(1)).findValidByToken(eq("tok-1"), any(OffsetDateTime.class));
  }

  @Test
  void shouldEvictCacheEntryOnRevoke() {
    final UserToken token = buildToken("tok-1", 1, OffsetDateTime.now().plusHours(1));
    cache.put("tok-1", token);

    repository.revoke("tok-1");

    verify(delegate).revoke("tok-1");
    assertThat(cache.get("tok-1")).isNull();
  }

  @Test
  void shouldEvictAllRevokedTokensForUser() {
    cache.put("tok-1", buildToken("tok-1", 1, OffsetDateTime.now().plusHours(1)));
    cache.put("tok-2", buildToken("tok-2", 1, OffsetDateTime.now().plusHours(1)));
    when(delegate.revokeAllForUser(1)).thenReturn(List.of("tok-1", "tok-2"));

    final List<String> revoked = repository.revokeAllForUser(1);

    assertThat(revoked).containsExactly("tok-1", "tok-2");
    assertThat(cache.get("tok-1")).isNull();
    assertThat(cache.get("tok-2")).isNull();
  }

  @Test
  void shouldDelegatePurgeExpiredWithoutTouchingCache() {
    final OffsetDateTime now = OffsetDateTime.now();
    cache.put("tok-1", buildToken("tok-1", 1, now.plusHours(1)));

    repository.purgeExpired(now);

    verify(delegate).purgeExpired(now);
    assertThat(cache.get("tok-1")).isNotNull();
  }

  @Test
  void shouldDelegateLockUser() {
    repository.lockUser(1);

    verify(delegate).lockUser(1);
  }
}
