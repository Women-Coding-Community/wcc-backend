package com.wcc.platform.repository.postgres;

import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Abstract class for Postgres integration tests that sets up the necessary database table before
 * each test.
 */
@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DefaultDatabaseSetup {

  @Container
  private static final PostgreSQLContainer<?> CONTAINER =
      new PostgreSQLContainer<>("postgres:15")
          .withDatabaseName("testdb")
          .withUsername("testuser")
          .withPassword("testpass");

  @BeforeAll
  /* default */ static void createTable(@Autowired final DataSource dataSource) {
    final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    jdbcTemplate.execute(
        "CREATE TABLE IF NOT EXISTS page(id TEXT PRIMARY KEY, data JSONB NOT NULL)");
  }

  @DynamicPropertySource
  /* default */ static void configureProperties(final DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", CONTAINER::getJdbcUrl);
    registry.add("spring.datasource.username", CONTAINER::getUsername);
    registry.add("spring.datasource.password", CONTAINER::getPassword);
  }
}
