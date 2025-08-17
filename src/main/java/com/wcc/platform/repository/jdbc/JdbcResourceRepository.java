package com.wcc.platform.repository.jdbc;

import com.wcc.platform.domain.resource.Resource;
import com.wcc.platform.domain.resource.ResourceType;
import com.wcc.platform.repository.ResourceRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/** JDBC implementation of the ResourceRepository interface. */
@Repository
@AllArgsConstructor
public class JdbcResourceRepository implements ResourceRepository {
  private static final String INSERT_SQL =
      "INSERT INTO resource (id, name, description, file_name, content_type, size, "
          + "drive_file_id, drive_file_link, resource_type_id, created_at, updated_at) "
          + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, "
          + "(SELECT id FROM resource_type WHERE name = ?), ?, ?)";
  private static final String UPDATE_SQL =
      "UPDATE resource SET name = ?, description = ?, file_name = ?, content_type = ?, "
          + "size = ?, drive_file_id = ?, drive_file_link = ?, "
          + "resource_type_id = (SELECT id FROM resource_type WHERE name = ?), "
          + "updated_at = ? WHERE id = ?";
  private static final String SELECT_BY_ID =
      "SELECT r.*, rt.name as resource_type_name FROM resource r "
          + "JOIN resource_type rt ON r.resource_type_id = rt.id "
          + "WHERE r.id = ?";
  private static final String SELECT_BY_TYPE =
      "SELECT r.*, rt.name as resource_type_name FROM resource r "
          + "JOIN resource_type rt ON r.resource_type_id = rt.id "
          + "WHERE rt.name = ?";
  private static final String SELECT_BY_NAME =
      "SELECT r.*, rt.name as resource_type_name FROM resource r "
          + "JOIN resource_type rt ON r.resource_type_id = rt.id "
          + "WHERE r.name ILIKE ?";
  private static final String DELETE_SQL = "DELETE FROM resource WHERE id = ?";
  private final JdbcTemplate jdbcTemplate;
  private final ResourceRowMapper rowMapper = new ResourceRowMapper();

  @SuppressWarnings("PMD.LawOfDemeter")
  @Override
  public Resource create(final Resource resource) {
    final var builder = resource.toBuilder();
    if (resource.getId() == null) {
      builder.id(UUID.randomUUID());
    }

    final OffsetDateTime now = OffsetDateTime.now();
    builder.createdAt(now);
    builder.updatedAt(now);

    final var resourceCreated = builder.build();

    jdbcTemplate.update(
        INSERT_SQL,
        resourceCreated.getId(),
        resourceCreated.getName(),
        resourceCreated.getDescription(),
        resourceCreated.getFileName(),
        resourceCreated.getContentType(),
        resourceCreated.getSize(),
        resourceCreated.getDriveFileId(),
        resourceCreated.getDriveFileLink(),
        resourceCreated.getResourceType().name(),
        resourceCreated.getCreatedAt(),
        resourceCreated.getUpdatedAt());

    return resourceCreated;
  }

  @SuppressWarnings("PMD.LawOfDemeter")
  @Override
  public Resource update(final UUID id, final Resource update) {
    final var resource = update.toBuilder().id(id).updatedAt(OffsetDateTime.now()).build();
    final var resourceType = resource.getResourceType();

    jdbcTemplate.update(
        UPDATE_SQL,
        resource.getName(),
        resource.getDescription(),
        resource.getFileName(),
        resource.getContentType(),
        resource.getSize(),
        resource.getDriveFileId(),
        resource.getDriveFileLink(),
        resourceType.name(),
        resource.getUpdatedAt(),
        id);

    return resource;
  }

  @Override
  public Optional<Resource> findById(final UUID id) {
    try {
      final var resource = jdbcTemplate.queryForObject(SELECT_BY_ID, rowMapper, id);
      return Optional.ofNullable(resource);
    } catch (DataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public void deleteById(final UUID id) {
    jdbcTemplate.update(DELETE_SQL, id);
  }

  @Override
  public List<Resource> findByType(final ResourceType resourceType) {
    return jdbcTemplate.query(SELECT_BY_TYPE, rowMapper, resourceType.name());
  }

  @Override
  public List<Resource> findByNameContaining(final String name) {
    return jdbcTemplate.query(SELECT_BY_NAME, rowMapper, "%" + name + "%");
  }

  /** RowMapper for mapping database rows to Resource objects. */
  private static final class ResourceRowMapper implements RowMapper<Resource> {
    @Override
    public Resource mapRow(final ResultSet rs, final int rowNum) throws SQLException {
      return Resource.builder()
          .id(UUID.fromString(rs.getString("id")))
          .name(rs.getString("name"))
          .description(rs.getString("description"))
          .fileName(rs.getString("file_name"))
          .contentType(rs.getString("content_type"))
          .size(rs.getLong("size"))
          .driveFileId(rs.getString("drive_file_id"))
          .driveFileLink(rs.getString("drive_file_link"))
          .resourceType(ResourceType.valueOf(rs.getString("resource_type_name")))
          .createdAt(rs.getObject("created_at", OffsetDateTime.class))
          .updatedAt(rs.getObject("updated_at", OffsetDateTime.class))
          .build();
    }
  }
}
