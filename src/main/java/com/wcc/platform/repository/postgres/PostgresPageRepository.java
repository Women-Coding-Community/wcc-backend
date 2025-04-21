package com.wcc.platform.repository.postgres;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.repository.PageRepository;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/** Postgres repository implementation for page. */
@Repository
@AllArgsConstructor
public class PostgresPageRepository implements PageRepository {

  private static final String TABLE = "page";

  private final JdbcTemplate jdbcTemplate;

  private final ObjectMapper objectMapper;

  @Override
  public Map<String, Object> create(final Map<String, Object> entity) {
    final String sql =
        "INSERT INTO " + TABLE + " (id, data) VALUES (?, to_jsonb(?::json)) RETURNING data";
    try {
      final String id = String.valueOf(entity.get("id"));
      final String data = objectMapper.writeValueAsString(entity);
      Map<String, Object> response = jdbcTemplate.queryForObject(sql, rowMapper(), id, data);
      String dataResponse = (String) response.get("data");
      return objectMapper.readValue(dataResponse, new TypeReference<>() {});
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException(e);
    }
  }

  @Override
  public Map<String, Object> update(final String id, final Map<String, Object> entity) {
    final String sql =
        "UPDATE " + TABLE + " SET data = to_jsonb(?::json) WHERE id = ? RETURNING data";

    try {
      entity.put("id", id);
      final String data = objectMapper.writeValueAsString(entity);
      Map<String, Object> result = jdbcTemplate.queryForObject(sql, rowMapper(), id, data);

      return result;
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException(e);
    }
  }

  @Override
  public Optional<Map<String, Object>> findById(final String id) {
    final String sql = "SELECT id, data FROM " + TABLE + " WHERE id = ?";
    Optional<Map<String, Object>> first =
        jdbcTemplate.query(sql, rowMapper(), id).stream().findFirst();

    if (first.isPresent()) {
      Map<String, Object> result = first.get();
      String data = (String) result.get("data");
      try {
        return Optional.of(objectMapper.readValue(data, new TypeReference<>() {}));
      } catch (JsonProcessingException e) {
        throw new IllegalArgumentException(e);
      }
    } else {
      return Optional.empty();
    }
  }

  @Override
  public void deleteById(final String id) {
    final String sql = "DELETE FROM " + TABLE + " WHERE id = ?";
    jdbcTemplate.update(sql, id);
  }

  private RowMapper<Map<String, Object>> rowMapper() {
    return (rs, rowNum) -> Map.of("id", rs.getString("id"), "data", rs.getString("data"));
  }
}
