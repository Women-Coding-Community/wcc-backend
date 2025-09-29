package com.wcc.platform.repository.postgres;

import com.wcc.platform.config.TestGoogleDriveConfig;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * Base configuration for Postgres-backed integration tests.
 * Uses a singleton Testcontainers PostgreSQL instance shared across the JVM
 * to avoid restarting the DB between test classes (prevents stale HikariCP connections).
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestGoogleDriveConfig.class)
public class DefaultDatabaseSetup {

  @DynamicPropertySource
  /* default */ static void configureProperties(final DynamicPropertyRegistry registry) {
    var container = PostgresTestContainer.getInstance();
    registry.add("spring.datasource.url", container::getJdbcUrl);
    registry.add("spring.datasource.username", container::getUsername);
    registry.add("spring.datasource.password", container::getPassword);
  }
}
