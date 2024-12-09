package com.wcc.platform.repository.surrealdb;

import com.surrealdb.driver.SyncSurrealDriver;
import com.wcc.platform.repository.PageRepository;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/** SurrealDB repository implementation for page. */
public class SurrealDbPageRepository implements PageRepository {

  /* default */ static final String TABLE = "page";

  private final SyncSurrealDriver driver;

  public SurrealDbPageRepository(final SyncSurrealDriver driver) {
    this.driver = driver;
  }

  @Override
  public String save(final String entity) {
    return driver.create(TABLE, entity);
  }

  @Override
  public Collection<String> findAll() {
    return driver.select(TABLE, String.class);
  }

  @Override
  public Optional<String> findById(final String id) {
    final var key = TABLE + ":" + id;
    final var query =
        driver.query("SELECT * FROM " + TABLE + " WHERE id = $id", Map.of("id", key), String.class);

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
