package com.wcc.platform.repository.postgres;

import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Singleton PostgreSQL Testcontainer to be shared across all integration tests.
 * This avoids restarting the DB between test classes, which can leave
 * stale connections in HikariCP when Spring reuses the application context.
 */
public final class PostgresTestContainer {

  private static final PostgreSQLContainer<?> INSTANCE =
      new PostgreSQLContainer<>("postgres:16-alpine")
          .withDatabaseName("testdb")
          .withUsername("testuser")
          .withPassword("testpass")
          .withStartupTimeout(java.time.Duration.ofSeconds(80))
          .withEnv("POSTGRES_HOST_AUTH_METHOD", "trust");

  static {
    INSTANCE.start();
  }

  private PostgresTestContainer() {
    // utility
  }

  public static PostgreSQLContainer<?> getInstance() {
    return INSTANCE;
  }
}
