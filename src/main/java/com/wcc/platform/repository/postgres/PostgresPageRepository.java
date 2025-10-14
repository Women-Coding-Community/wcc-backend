package com.wcc.platform.repository.postgres;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.exceptions.DuplicatedItemException;
import com.wcc.platform.repository.PageRepository;
import com.wcc.platform.utils.JsonUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/** Postgres repository implementation for page supporting also H2/DB2. */
@Primary
@Repository
public class PostgresPageRepository implements PageRepository {
  private static final String TABLE = "page";
  private static final String COLUMN = "data";

  private final JdbcTemplate jdbc;
  private final ObjectMapper mapper;
  private final boolean isPostgres;

  /** Constructor. */
  @Autowired
  public PostgresPageRepository(
      final JdbcTemplate jdbc,
      final ObjectMapper mapper,
      final @Value("${spring.datasource.driver-class-name}") String driverClassName) {
    this.jdbc = jdbc;
    this.mapper = mapper;
    isPostgres = driverClassName.contains("postgres");
  }

  @Override
  public Map<String, Object> create(final Map<String, Object> entity) {
    final String id = String.valueOf(entity.get("id"));

    try {
      final var data = mapper.writeValueAsString(entity);

      if (isPostgres) {
        final String sql =
            "INSERT INTO " + TABLE + " (id, data) VALUES (?, to_jsonb(?::json)) RETURNING id, data";
        final var dataResponse =
            (String) jdbc.queryForObject(sql, rowMapper(), id, data).get(COLUMN);

        return mapper.readValue(JsonUtil.normalizeJson(dataResponse), new TypeReference<>() {});
      } else {
        final String sql = "INSERT INTO " + TABLE + " (id, data) VALUES (?, ?)";
        jdbc.update(sql, id, data);
        return findById(id).orElseThrow();
      }
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException(e);
    } catch (DuplicateKeyException e) {
      throw new DuplicatedItemException("Duplicated item with id: " + id, e);
    }
  }

  @Override
  public Map<String, Object> update(final String id, final Map<String, Object> entity) {
    try {
      final var toStore = new HashMap<>(entity);
      toStore.put("id", id);

      final String data = mapper.writeValueAsString(toStore);

      if (isPostgres) {
        final String sql =
            "UPDATE " + TABLE + " SET data = to_jsonb(?::json) WHERE id = ? RETURNING id, data";
        final var row = jdbc.queryForObject(sql, rowMapper(), data, id);
        final var dataResponse = (String) row.get(COLUMN);
        return mapper.readValue(dataResponse, new TypeReference<>() {});
      } else {
        final String sql = "UPDATE " + TABLE + " SET data = ? WHERE id = ?";
        jdbc.update(sql, data, id);
        return findById(id).orElseThrow();
      }
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
      final String raw = (String) first.get().get(COLUMN);
      try {
        final var jsonData = JsonUtil.normalizeJson(raw);
        return Optional.of(mapper.readValue(jsonData, new TypeReference<>() {}));
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
