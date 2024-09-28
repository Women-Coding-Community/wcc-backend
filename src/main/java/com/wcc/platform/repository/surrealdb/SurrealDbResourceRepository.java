package com.wcc.platform.repository.surrealdb;

import com.surrealdb.driver.SyncSurrealDriver;
import com.wcc.platform.domain.platform.ResourceContent;
import com.wcc.platform.repository.ResourceContentRepository;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/** SurrealDB repository implementation for resources. */
@Repository
public class SurrealDbResourceRepository implements ResourceContentRepository {

  static final String TABLE = "resource_content";
  private final SyncSurrealDriver driver;

  @Autowired
  public SurrealDbResourceRepository(final SyncSurrealDriver driver) {
    this.driver = driver;
  }

  @Override
  public ResourceContent save(final ResourceContent entity) {
    return driver.create(TABLE, entity);
  }

  @Override
  public Collection<ResourceContent> findAll() {
    return driver.select(TABLE, ResourceContent.class);
  }

  @Override
  public Optional<ResourceContent> findById(final String id) {
    final var query =
        driver.query(
            "SELECT * FROM " + TABLE + " WHERE id = $id", Map.of("id", id), ResourceContent.class);

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
