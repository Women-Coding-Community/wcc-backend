package com.wcc.platform.repository.surrealdb;

import com.surrealdb.driver.SyncSurrealDriver;
import com.wcc.platform.repository.PageRepository;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/** SurrealDB repository implementation for page. */
@Repository
public class SurrealDbPageRepository implements PageRepository {

  private static final String TABLE = "page";
  private final SyncSurrealDriver driver;

  @Autowired
  public SurrealDbPageRepository(final SyncSurrealDriver driver) {
    this.driver = driver;
  }

  @Override
  public Object save(final Object entity) {
    return driver.create(TABLE, entity);
  }

  @Override
  public Collection<Object> findAll() {
    return driver.select(TABLE, Object.class);
  }

  @Override
  public Optional<Object> findById(final String id) {
    final var query =
        driver.query("SELECT * FROM " + TABLE + " WHERE id = $id", Map.of("id", id), Object.class);

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
