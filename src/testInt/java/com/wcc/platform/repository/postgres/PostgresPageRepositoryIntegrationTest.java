package com.wcc.platform.repository.postgres;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PostgresPageRepositoryIntegrationTest extends DefaultDatabaseSetup {

  public static final String NAME = "name";
  @Autowired private PostgresPageRepository repository;

  @Test
  void createShouldPersistEntity() {
    final Map<String, Object> entity = Map.of("id", "1", NAME, "Test Page");

    final Map<String, Object> result = repository.create(entity);

    assertEquals(entity, result, "Should return existent entity");
  }

  @Test
  void findByIdShouldReturnEntityWhenExists() {
    final Map<String, Object> entity = Map.of("id", "2", NAME, "Another Page");
    repository.create(entity);

    final var result = repository.findById("2");

    assertEquals(entity, result.get(), "Should return the existent entity");
  }

  @Test
  void findByIdShouldReturnEmptyWhenNotExists() {
    final var result = repository.findById("999");

    assertTrue(result.isEmpty(), "Should return empty when entity does not exist");
  }

  @Test
  @Disabled
  void updateShouldModifyExistingEntity() {
    final Map<String, Object> entity = Map.of("id", "3", NAME, "Old Page");
    repository.create(entity);

    final Map<String, Object> result = repository.update("3", Map.of(NAME, "Updated Page"));

    assertEquals(
        "{\"id\": \"3\", \"name\": \"Updated Page\"}",
        result.get("data"),
        "Should update entity data");
  }

  @Test
  void deleteByIdShouldRemoveEntity() {
    final Map<String, Object> entity = Map.of("id", "4", NAME, "Page to Delete");
    repository.create(entity);

    repository.deleteById("4");

    final Optional<Map<String, Object>> result = repository.findById("4");
    assertTrue(result.isEmpty(), "Entity should be deleted");
  }
}
