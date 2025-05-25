package com.wcc.platform.repository.postgres;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class PostgresPageRepositoryIntegrationTest extends AbstractDatabaseIntegrationTest {

  @Autowired private PostgresPageRepository repository;

  @Test
  void createShouldPersistEntity() {
    Map<String, Object> entity = Map.of("id", "1", "name", "Test Page");

    Map<String, Object> result = repository.create(entity);

    assertEquals("1", result.get("id"));
    assertEquals("Test Page", result.get("name"));
  }

  @Test
  void findByIdShouldReturnEntityWhenExists() {
    Map<String, Object> entity = Map.of("id", "2", "name", "Another Page");
    repository.create(entity);

    var result = repository.findById("2");

    assertTrue(result.isPresent());
    assertEquals("Another Page", result.get().get("name"));
  }

  @Test
  void findByIdShouldReturnEmptyWhenNotExists() {
    Optional<Map<String, Object>> result = repository.findById("999");

    assertTrue(result.isEmpty());
  }

  @Test
  void updateShouldModifyExistingEntity() {
    Map<String, Object> entity = Map.of("id", "3", "name", "Old Page");
    repository.create(entity);

    Map<String, Object> updatedEntity = new java.util.HashMap<>();
    updatedEntity.put("name", "Updated Page");
    Map<String, Object> result = repository.update("3", updatedEntity);

    assertEquals("{\"id\": \"3\", \"name\": \"Updated Page\"}", result.get("data"));
  }

  @Test
  void deleteByIdShouldRemoveEntity() {
    Map<String, Object> entity = Map.of("id", "4", "name", "Page to Delete");
    repository.create(entity);

    repository.deleteById("4");

    Optional<Map<String, Object>> result = repository.findById("4");
    assertTrue(result.isEmpty());
  }
}
