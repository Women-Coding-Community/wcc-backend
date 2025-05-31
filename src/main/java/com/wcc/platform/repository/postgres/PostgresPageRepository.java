package com.wcc.platform.repository.postgres;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.exceptions.DuplicatedItemException;
import com.wcc.platform.repository.PageRepository;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/** Postgres repository implementation for page. */
@Repository
@AllArgsConstructor
public class PostgresPageRepository implements PageRepository {

  private static final String TABLE = "page";
  private static final String COLUMN = "data";

  private final JdbcTemplate jdbc;

  private final ObjectMapper mapper;

  @Override
  public Map<String, Object> create(final Map<String, Object> entity) {
    final String sql =
        "INSERT INTO " + TABLE + " (id, data) VALUES (?, to_jsonb(?::json)) RETURNING id, data";

    final String id = String.valueOf(entity.get("id"));

    try {
      final var data = mapper.writeValueAsString(entity);
      final var dataResponse = (String) jdbc.queryForObject(sql, rowMapper(), id, data).get(COLUMN);
      return mapper.readValue(dataResponse, new TypeReference<>() {});
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException(e);
    } catch (DuplicateKeyException e) {
      throw new DuplicatedItemException("Duplicated item with id: " + id, e);
    }
  }

  @Override
  public Map<String, Object> update(final String id, final Map<String, Object> entity) {
    final String sql = "UPDATE page SET data = to_jsonb(?::json) WHERE id = ? RETURNING id, data";

    try {
      entity.put("id", id);

      final String data = mapper.writeValueAsString(entity);

      return jdbc.queryForObject(sql, rowMapper(), data, id);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException(e);
    }
  }

  @Override
  public Optional<Map<String, Object>> findById(final String id) {
    final String sql = "SELECT id, data FROM " + TABLE + " WHERE id = ?";
    final Optional<Map<String, Object>> first =
        jdbc.query(sql, rowMapper(), id).stream().findFirst();

    if (first.isPresent()) {
      final String data = (String) first.get().get(COLUMN);
      try {
        return Optional.of(mapper.readValue(data, new TypeReference<>() {}));
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
    jdbc.update(sql, id);
  }

  private RowMapper<Map<String, Object>> rowMapper() {
    return (rs, rowNum) -> Map.of("id", rs.getString("id"), COLUMN, rs.getString(COLUMN));
  }
}
