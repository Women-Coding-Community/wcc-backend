package com.wcc.platform.repository.file;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FilePageRepositoryTest {

  private FilePageRepository repository;

  @BeforeEach
  void setUp() {
    repository = new FilePageRepository();
  }

  private Map<String, Object> createTestEntity() {
    return Map.of(
        "id", "test-id",
        "title", "Test Page",
        "content", "Test Content");
  }

  @Test
  void createShouldReturnEmptyMap() {
    Map<String, Object> entity = createTestEntity();
    Map<String, Object> result = repository.create(entity);

    assertTrue(result.isEmpty());
  }

  @Test
  void updateShouldReturnEmptyMap() {
    Map<String, Object> entity = createTestEntity();
    Map<String, Object> result = repository.update("test-id", entity);

    assertTrue(result.isEmpty());
  }

  @Test
  void findByIdShouldReturnEmptyOptional() {
    Optional<Map<String, Object>> result = repository.findById("test-id");

    assertTrue(result.isEmpty());
  }

  @Test
  void deleteByIdShouldNotThrowException() {
    assertDoesNotThrow(() -> repository.deleteById("test-id"));
  }
}
