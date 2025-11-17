package com.wcc.platform.repository.postgres;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

@ExtendWith(MockitoExtension.class)
class PostgresPageRepositoryTest {
  @Mock private JdbcTemplate jdbc;
  @Mock private ObjectMapper mapper;

  @Test
  void shouldPersistAndReturnEntity() throws Exception {
    final String pageId = "1";
    final Map<String, Object> entity = Map.of("id", pageId, "name", "Test Page");
    final String json = "{\"id\":\"1\",\"name\":\"Test Page\"}";

    when(mapper.writeValueAsString(entity)).thenReturn(json);

    when(jdbc.queryForObject(anyString(), any(RowMapper.class), eq(pageId), eq(json)))
        .thenReturn(Map.of("id", pageId, "data", json));

    when(mapper.readValue(anyString(), ArgumentMatchers.<TypeReference<Map<String, Object>>>any()))
        .thenReturn(entity);

    PostgresPageRepository repo = new PostgresPageRepository(jdbc, mapper, "org.postgresql.Driver");

    Map<String, Object> result = repo.create(entity);

    assertEquals(entity, result);
  }

  @Test
  void findByIdShouldReturnOptionalEmptyNoRow() {
    final String pageId = "999";
    when(jdbc.query(anyString(), any(RowMapper.class), eq(pageId))).thenReturn(List.of());

    PostgresPageRepository repo = new PostgresPageRepository(jdbc, mapper, "org.postgresql.Driver");

    Optional<Map<String, Object>> result = repo.findById(pageId);

    assertTrue(result.isEmpty());
  }

  @Test
  void deleteByIdWithUpdate() {
    final String pageId = "4";
    PostgresPageRepository repo = new PostgresPageRepository(jdbc, mapper, "org.postgresql.Driver");

    repo.deleteById(pageId);

    verify(jdbc).update(contains("DELETE FROM"), eq(pageId));
  }
}
