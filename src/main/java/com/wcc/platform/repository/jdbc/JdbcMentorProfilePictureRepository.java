package com.wcc.platform.repository.jdbc;

import com.wcc.platform.domain.resource.MentorProfilePicture;
import com.wcc.platform.domain.resource.Resource;
import com.wcc.platform.domain.resource.ResourceType;
import com.wcc.platform.repository.MentorProfilePictureRepository;
import com.wcc.platform.repository.ResourceRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/** JDBC implementation of the MentorProfilePictureRepository interface. */
@Repository
public class JdbcMentorProfilePictureRepository implements MentorProfilePictureRepository {
  private static final String INSERT_SQL =
      "INSERT INTO mentor_profile_picture (id, mentor_email, resource_id, created_at, updated_at) "
          + "VALUES (?, ?, ?, ?, ?)";
  private static final String UPDATE_SQL =
      "UPDATE mentor_profile_picture SET mentor_email = ?, resource_id = ?, updated_at = ? "
          + "WHERE id = ?";
  private static final String SELECT_BY_ID_SQL =
      "SELECT mpp.*, r.*, rt.name as resource_type_name "
          + "FROM mentor_profile_picture mpp "
          + "JOIN resource r ON mpp.resource_id = r.id "
          + "JOIN resource_type rt ON r.resource_type_id = rt.id "
          + "WHERE mpp.id = ?";
  private static final String SELECT_BY_EMAIL_SQL =
      "SELECT mpp.*, r.*, rt.name as resource_type_name "
          + "FROM mentor_profile_picture mpp "
          + "JOIN resource r ON mpp.resource_id = r.id "
          + "JOIN resource_type rt ON r.resource_type_id = rt.id "
          + "WHERE mpp.mentor_email = ?";
  private static final String DELETE_SQL = "DELETE FROM mentor_profile_picture WHERE id = ?";
  private static final String DELETE_BY_EMAIL_SQL =
      "DELETE FROM mentor_profile_picture WHERE mentor_email = ?";
  private final JdbcTemplate jdbcTemplate;
  private final ResourceRepository resourceRepository;
  private final MentorProfilePictureRowMapper rowMapper = new MentorProfilePictureRowMapper();

  public JdbcMentorProfilePictureRepository(
      JdbcTemplate jdbcTemplate, ResourceRepository resourceRepository) {
    this.jdbcTemplate = jdbcTemplate;
    this.resourceRepository = resourceRepository;
  }

  @Override
  public MentorProfilePicture create(MentorProfilePicture profilePicture) {
    if (profilePicture.getId() == null) {
      profilePicture.setId(UUID.randomUUID());
    }

    OffsetDateTime now = OffsetDateTime.now();
    profilePicture.setCreatedAt(now);
    profilePicture.setUpdatedAt(now);

    jdbcTemplate.update(
        INSERT_SQL,
        profilePicture.getId(),
        profilePicture.getMentorEmail(),
        profilePicture.getResourceId(),
        profilePicture.getCreatedAt(),
        profilePicture.getUpdatedAt());

    return profilePicture;
  }

  @Override
  public MentorProfilePicture update(UUID id, MentorProfilePicture profilePicture) {
    profilePicture.setId(id);
    profilePicture.setUpdatedAt(OffsetDateTime.now());

    jdbcTemplate.update(
        UPDATE_SQL,
        profilePicture.getMentorEmail(),
        profilePicture.getResourceId(),
        profilePicture.getUpdatedAt(),
        id);

    return profilePicture;
  }

  @Override
  public Optional<MentorProfilePicture> findById(UUID id) {
    try {
      MentorProfilePicture profilePicture =
          jdbcTemplate.queryForObject(SELECT_BY_ID_SQL, rowMapper, id);
      return Optional.ofNullable(profilePicture);
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  @Override
  public void deleteById(UUID id) {
    jdbcTemplate.update(DELETE_SQL, id);
  }

  @Override
  public Optional<MentorProfilePicture> findByMentorEmail(String email) {
    try {
      MentorProfilePicture profilePicture =
          jdbcTemplate.queryForObject(SELECT_BY_EMAIL_SQL, rowMapper, email);
      return Optional.ofNullable(profilePicture);
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  @Override
  public void deleteByMentorEmail(String email) {
    jdbcTemplate.update(DELETE_BY_EMAIL_SQL, email);
  }

  /** RowMapper for mapping database rows to MentorProfilePicture objects. */
  private class MentorProfilePictureRowMapper implements RowMapper<MentorProfilePicture> {
    @Override
    public MentorProfilePicture mapRow(ResultSet rs, int rowNum) throws SQLException {
      Resource resource =
          Resource.builder()
              .id(UUID.fromString(rs.getString("resource_id")))
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

      return MentorProfilePicture.builder()
          .id(UUID.fromString(rs.getString("id")))
          .mentorEmail(rs.getString("mentor_email"))
          .resourceId(UUID.fromString(rs.getString("resource_id")))
          .resource(resource)
          .createdAt(rs.getObject("created_at", OffsetDateTime.class))
          .updatedAt(rs.getObject("updated_at", OffsetDateTime.class))
          .build();
    }
  }
}
