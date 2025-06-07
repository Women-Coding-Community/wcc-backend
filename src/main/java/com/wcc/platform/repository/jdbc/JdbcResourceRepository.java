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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/** JDBC implementation of the ResourceRepository interface. */
@Repository
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
  private static final String SELECT_BY_ID_SQL =
      "SELECT r.*, rt.name as resource_type_name FROM resource r "
          + "JOIN resource_type rt ON r.resource_type_id = rt.id "
          + "WHERE r.id = ?";
  private static final String SELECT_BY_TYPE_SQL =
      "SELECT r.*, rt.name as resource_type_name FROM resource r "
          + "JOIN resource_type rt ON r.resource_type_id = rt.id "
          + "WHERE rt.name = ?";
  private static final String SELECT_BY_NAME_SQL =
      "SELECT r.*, rt.name as resource_type_name FROM resource r "
          + "JOIN resource_type rt ON r.resource_type_id = rt.id "
          + "WHERE r.name ILIKE ?";
  private static final String DELETE_SQL = "DELETE FROM resource WHERE id = ?";
  private final JdbcTemplate jdbcTemplate;
  private final ResourceRowMapper rowMapper = new ResourceRowMapper();

  public JdbcResourceRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public Resource create(Resource resource) {
    if (resource.getId() == null) {
      resource.setId(UUID.randomUUID());
    }

    OffsetDateTime now = OffsetDateTime.now();
    resource.setCreatedAt(now);
    resource.setUpdatedAt(now);

    jdbcTemplate.update(
        INSERT_SQL,
        resource.getId(),
        resource.getName(),
        resource.getDescription(),
        resource.getFileName(),
        resource.getContentType(),
        resource.getSize(),
        resource.getDriveFileId(),
        resource.getDriveFileLink(),
        resource.getResourceType().name(),
        resource.getCreatedAt(),
        resource.getUpdatedAt());

    return resource;
  }

  @Override
  public Resource update(UUID id, Resource resource) {
    resource.setId(id);
    resource.setUpdatedAt(OffsetDateTime.now());

    jdbcTemplate.update(
        UPDATE_SQL,
        resource.getName(),
        resource.getDescription(),
        resource.getFileName(),
        resource.getContentType(),
        resource.getSize(),
        resource.getDriveFileId(),
        resource.getDriveFileLink(),
        resource.getResourceType().name(),
        resource.getUpdatedAt(),
        id);

    return resource;
  }

  @Override
  public Optional<Resource> findById(UUID id) {
    try {
      Resource resource = jdbcTemplate.queryForObject(SELECT_BY_ID_SQL, rowMapper, id);
      return Optional.ofNullable(resource);
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  @Override
  public void deleteById(UUID id) {
    jdbcTemplate.update(DELETE_SQL, id);
  }

  @Override
  public List<Resource> findByType(ResourceType resourceType) {
    return jdbcTemplate.query(SELECT_BY_TYPE_SQL, rowMapper, resourceType.name());
  }

  @Override
  public List<Resource> findByNameContaining(String name) {
    return jdbcTemplate.query(SELECT_BY_NAME_SQL, rowMapper, "%" + name + "%");
  }

  /** RowMapper for mapping database rows to Resource objects. */
  private static class ResourceRowMapper implements RowMapper<Resource> {
    @Override
    public Resource mapRow(ResultSet rs, int rowNum) throws SQLException {
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
