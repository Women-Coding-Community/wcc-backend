package com.wcc.platform.repository.surrealdb;

import com.surrealdb.driver.SyncSurrealDriver;
import com.wcc.platform.repository.PageRepository;
import java.util.Map;
import java.util.Optional;

/** SurrealDB repository implementation for page. */
@SuppressWarnings("unchecked")
public class SurrealDbPageRepository implements PageRepository {

  /* default */ static final String TABLE = "page";

  private final SyncSurrealDriver driver;

  public SurrealDbPageRepository(final SyncSurrealDriver driver) {
    this.driver = driver;
  }

  @Override
  public Map<String, Object> save(final Map<String, Object> entity) {
    return driver.create(TABLE, entity);
  }

  @Override
  public Optional<Map<String, Object>> findById(final String id) {
    final var query =
        driver.query("SELECT * FROM " + TABLE + " WHERE id = $id", Map.of("id", id), Map.class);

    if (query.isEmpty()) {
      return Optional.empty();
    }

    final var result = query.getFirst().getResult();
    if (result.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(result.getFirst());
  }

  @Override
  public void deleteById(final String id) {
    driver.delete(id);
  }
}
