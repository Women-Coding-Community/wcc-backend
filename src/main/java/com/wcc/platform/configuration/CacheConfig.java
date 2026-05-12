package com.wcc.platform.configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configuration for application-level caching using Caffeine. */
@Configuration
@EnableCaching
public class CacheConfig {

  /**
   * Configures a CacheManager using Caffeine with default settings for mentorship-related data.
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
    return cacheManager;
  }
}
