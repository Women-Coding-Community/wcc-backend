package com.wcc.platform.repository.jdbc;

import com.wcc.platform.domain.platform.type.ContentType;
import com.wcc.platform.domain.platform.type.ResourceType;
import com.wcc.platform.domain.resource.MentorProfilePicture;
import com.wcc.platform.domain.resource.Resource;
import com.wcc.platform.repository.MentorProfilePictureRepository;
import com.wcc.platform.repository.ResourceRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/** JDBC implementation of the MentorProfilePictureRepository interface. */
@Repository
@AllArgsConstructor
public class JdbcMentorProfilePictureRepository implements MentorProfilePictureRepository {
  private static final String INSERT_SQL =
      "INSERT INTO mentor_profile_picture (id, mentor_email, resource_id, created_at, updated_at) "
          + "VALUES (?, ?, ?, ?, ?)";
  private static final String UPDATE_SQL =
      "UPDATE mentor_profile_picture SET mentor_email = ?, resource_id = ?, updated_at = ? "
          + "WHERE id = ?";
  private static final String SELECT_BY_ID =
      "SELECT mpp.*, r.*, rt.name as resource_type_name "
          + "FROM mentor_profile_picture mpp "
          + "JOIN resource r ON mpp.resource_id = r.id "
          + "JOIN resource_type rt ON r.resource_type_id = rt.id "
          + "WHERE mpp.id = ?";
  private static final String SELECT_BY_EMAIL =
      "SELECT mpp.*, r.*, rt.name as resource_type_name "
          + "FROM mentor_profile_picture mpp "
          + "JOIN resource r ON mpp.resource_id = r.id "
          + "JOIN resource_type rt ON r.resource_type_id = rt.id "
          + "WHERE mpp.mentor_email = ?";
  private static final String SEL_BY_ID =
      "SELECT mpp.*, r.*, rt.name as resource_type_name "
          + "FROM mentor_profile_picture mpp "
          + "JOIN resource r ON mpp.resource_id = r.id "
          + "JOIN resource_type rt ON r.resource_type_id = rt.id "
          + "WHERE mpp.resource_id = ?";
  private static final String DELETE_SQL = "DELETE FROM mentor_profile_picture WHERE id = ?";
  private static final String DELETE_BY_EMAIL =
      "DELETE FROM mentor_profile_picture WHERE mentor_email = ?";
  private static final String DEL_BY_ID =
      "DELETE FROM mentor_profile_picture WHERE resource_id = ?";
  private final JdbcTemplate jdbcTemplate;
  private final ResourceRepository repository;
  private final MentorProfilePictureRowMapper rowMapper = new MentorProfilePictureRowMapper();

  @Override
  public MentorProfilePicture create(final MentorProfilePicture profilePicture) {
    if (profilePicture.getId() == null) {
      profilePicture.setId(UUID.randomUUID());
    }

    final OffsetDateTime now = OffsetDateTime.now();
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
  public MentorProfilePicture update(final UUID id, final MentorProfilePicture profilePicture) {
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
  public Optional<MentorProfilePicture> findById(final UUID id) {
    try {
      final MentorProfilePicture profilePicture =
          jdbcTemplate.queryForObject(SELECT_BY_ID, rowMapper, id);
      return Optional.ofNullable(profilePicture);
    } catch (DataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public void deleteById(final UUID id) {
    jdbcTemplate.update(DELETE_SQL, id);
  }

  @Override
  public Optional<MentorProfilePicture> findByMentorEmail(final String email) {
    try {
      final MentorProfilePicture profilePicture =
          jdbcTemplate.queryForObject(SELECT_BY_EMAIL, rowMapper, email);
      return Optional.ofNullable(profilePicture);
    } catch (DataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public void deleteByMentorEmail(final String email) {
    jdbcTemplate.update(DELETE_BY_EMAIL, email);
  }

  @Override
  public Optional<MentorProfilePicture> findByResourceId(final UUID resourceId) {
    try {
      final MentorProfilePicture profilePicture =
          jdbcTemplate.queryForObject(SEL_BY_ID, rowMapper, resourceId);
      return Optional.ofNullable(profilePicture);
    } catch (DataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public void deleteByResourceId(final UUID resourceId) {
    jdbcTemplate.update(DEL_BY_ID, resourceId);
  }

  /** RowMapper for mapping database rows to MentorProfilePicture objects. */
  private static final class MentorProfilePictureRowMapper
      implements RowMapper<MentorProfilePicture> {
    @Override
    public MentorProfilePicture mapRow(final ResultSet rs, final int rowNum) throws SQLException {
      final Resource resource =
          Resource.builder()
              .id(UUID.fromString(rs.getString("resource_id")))
              .name(rs.getString("name"))
              .description(rs.getString("description"))
              .fileName(rs.getString("file_name"))
              .contentType(ContentType.valueOf(rs.getString("content_type")))
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
