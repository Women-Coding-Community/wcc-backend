package com.wcc.platform.repository.surrealdb;

import com.surrealdb.driver.SyncSurrealDriver;
import com.wcc.platform.repository.PageRepository;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/** SurrealDB repository implementation for page. */
public class SurrealDbPageRepository<T> implements PageRepository<T> {

  /* default */ static final String TABLE = "page";

  private final SyncSurrealDriver driver;
  private final Class<T> entityType;

  public SurrealDbPageRepository(final SyncSurrealDriver driver, final Class<T> entityType) {
    this.driver = driver;
    this.entityType = entityType;
  }

  @Override
  public T save(final T entity) {
    return driver.create(TABLE, entity);
  }

  @Override
  public Collection<T> findAll() {
    return driver.select(TABLE, entityType);
  }

  @Override
  public Optional<T> findById(final String id) {
    final var key = TABLE + ":" + id;
    final var query =
        driver.query("SELECT * FROM " + TABLE + " WHERE id = $id", Map.of("id", key), entityType);

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
