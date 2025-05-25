package com.wcc.platform.repository.postgres;

import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@ActiveProfiles("testIntegration")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class PostgresPageRepositoryIntegrationTest {

  @Container
  private static final PostgreSQLContainer<?> postgresContainer =
      new PostgreSQLContainer<>("postgres:15")
          .withDatabaseName("testdb")
          .withUsername("testuser")
          .withPassword("testpass");

  @Autowired private PostgresPageRepository repository;

  @BeforeAll
  static void createTable(@Autowired DataSource dataSource) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    jdbcTemplate.execute(
        "CREATE TABLE IF NOT EXISTS page(id TEXT PRIMARY KEY, data JSONB NOT NULL)");
  }

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
    registry.add("spring.datasource.username", postgresContainer::getUsername);
    registry.add("spring.datasource.password", postgresContainer::getPassword);
  }

  @Test
  void createShouldPersistEntity() {
    Map<String, Object> entity = Map.of("id", "1", "name", "Test Page");

    Map<String, Object> result = repository.create(entity);

    Assertions.assertEquals("1", result.get("id"));
    Assertions.assertEquals("Test Page", result.get("name"));
  }

  @Test
  void findByIdShouldReturnEntityWhenExists() {
    Map<String, Object> entity = Map.of("id", "2", "name", "Another Page");
    repository.create(entity);

    var result = repository.findById("2");

    Assertions.assertTrue(result.isPresent());
    Assertions.assertEquals("Another Page", result.get().get("name"));
  }

  @Test
  void findByIdShouldReturnEmptyWhenNotExists() {
    Optional<Map<String, Object>> result = repository.findById("999");

    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  void updateShouldModifyExistingEntity() {
    Map<String, Object> entity = Map.of("id", "3", "name", "Old Page");
    repository.create(entity);

    Map<String, Object> updatedEntity = new java.util.HashMap<>();
    updatedEntity.put("name", "Updated Page");
    Map<String, Object> result = repository.update("3", updatedEntity);

    Assertions.assertEquals("{\"id\": \"3\", \"name\": \"Updated Page\"}", result.get("data"));
  }

  @Test
  void deleteByIdShouldRemoveEntity() {
    Map<String, Object> entity = Map.of("id", "4", "name", "Page to Delete");
    repository.create(entity);

    repository.deleteById("4");

    Optional<Map<String, Object>> result = repository.findById("4");
    Assertions.assertTrue(result.isEmpty());
  }
}
