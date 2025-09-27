package com.wcc.platform.repository.file;

import com.wcc.platform.repository.PageRepository;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * FilePageRepository is an implementation of the PageRepository interface used for managing page
 * entity data stored in a file-based repository as fallback to postgres.
 */
@Repository
public class FilePageRepository implements PageRepository {

  @Override
  public Map<String, Object> create(final Map<String, Object> entity) {
    return Map.of();
  }

  @Override
  public Map<String, Object> update(final String id, final Map<String, Object> entity) {
    return Map.of();
  }

  @Override
  public Optional<Map<String, Object>> findById(final String id) {
    return Optional.empty();
  }

  @Override
  public void deleteById(final String id) {
    // not implemented
  }
}
