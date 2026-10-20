package com.wcc.platform.configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configuration for application-level caching using Caffeine. */
@Configuration
@EnableCaching
public class CacheConfig {

  /** Name of the cache holding the single active {@code UserToken} per user session. */
  public static final String USER_TOKENS_CACHE = "activeUserTokens";

  private static final int USER_TOKENS_MAX_SIZE = 10_000;

  private final int tokenTtlMinutes;

  public CacheConfig(@Value("${security.token.ttl-minutes}") final int tokenTtlMinutes) {
    this.tokenTtlMinutes = tokenTtlMinutes;
  }

  /**
   * Configures a CacheManager using Caffeine with default settings for mentorship-related data,
   * plus a dedicated {@value #USER_TOKENS_CACHE} cache whose entries expire on the same
   * schedule as {@code security.token.ttl-minutes} so it never outlives the underlying DB rows.
   *
   * @return the configured CacheManager
   */
  @Bean
  public CacheManager cacheManager() {
    final CaffeineCacheManager cacheManager =
        new CaffeineCacheManager(
            "mentorsAvailable", "mentorsStatus", "unmatchedMentees", "menteeApplications");
    cacheManager.setCaffeine(
        Caffeine.newBuilder().expireAfterWrite(20, TimeUnit.MINUTES).maximumSize(500));

    cacheManager.registerCustomCache(
        USER_TOKENS_CACHE,
        Caffeine.newBuilder()
            .expireAfterWrite(tokenTtlMinutes, TimeUnit.MINUTES)
            .maximumSize(USER_TOKENS_MAX_SIZE)
            .build());

    return cacheManager;
  }
}
