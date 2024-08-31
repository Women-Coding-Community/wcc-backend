package com.wcc.platform.repository.surrealdb;

import com.surrealdb.driver.SyncSurrealDriver;
import com.wcc.platform.domain.platform.ResourceContent;
import com.wcc.platform.repository.ResourceContentRepository;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SurrealDbResourceRepository implements ResourceContentRepository {

  private static final String TABLE = "resource_content";
  private final SyncSurrealDriver driver;

  @Autowired
  public SurrealDbResourceRepository(final DBConnection connection) {
    this.driver = connection.getDriver();
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
  public Optional<ResourceContent> findById(final UUID uuid) {
    var query =
        driver.query(
            "SELECT id FROM " + TABLE + " WHERE id=$id LIMIT BY 1;",
            Map.of("id", uuid.toString()),
            ResourceContent.class);

    if (query.isEmpty()) {
      return Optional.empty();
    }

    final var result = query.getFirst().getResult();
    if (result.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(result.getFirst());
  }
}
