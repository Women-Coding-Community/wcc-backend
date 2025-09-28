package com.wcc.platform.repository.postgres;

import static com.wcc.platform.repository.postgres.PostgresMemberRepository.MEMBER_ID_COLUMN;
import static com.wcc.platform.repository.postgres.PostgresResourceRepository.RESOURCE_ID_COLUMN;

import com.wcc.platform.domain.platform.type.ResourceType;
import com.wcc.platform.domain.resource.MemberProfilePicture;
import com.wcc.platform.domain.resource.Resource;
import com.wcc.platform.repository.MemberProfilePictureRepository;
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
public class PostgresMemberProfilePictureRepository implements MemberProfilePictureRepository {
  private static final String INSERT_SQL =
      "INSERT INTO member_profile_picture (member_id, resource_id) VALUES (?, ?)";
  private static final String SELECT_BY_MEMBER_ID =
      "SELECT mpp.*, r.* FROM member_profile_picture mpp "
          + "LEFT JOIN resource r ON mpp.resource_id = r.id "
          + "WHERE mpp.member_id = ?";
  private static final String SELECT_BY_RESOURCE_ID =
      "SELECT mpp.*, r.* FROM member_profile_picture mpp "
          + "LEFT JOIN resource r ON mpp.resource_id = r.id "
          + "WHERE mpp.resource_id = ?";
  private static final String DEL_BY_RESOURCE_ID =
      "DELETE FROM member_profile_picture WHERE resource_id = ?";
  private static final String DEL_BY_MEMBER_ID =
      "DELETE FROM member_profile_picture WHERE member_id = ?";
  private final JdbcTemplate jdbcTemplate;
  private final MemberProfilePictureRowMapper rowMapper = new MemberProfilePictureRowMapper();

  @Override
  public MemberProfilePicture create(final MemberProfilePicture profilePicture) {

    jdbcTemplate.update(INSERT_SQL, profilePicture.getMemberId(), profilePicture.getResourceId());

    return profilePicture;
  }

  @Override
  public MemberProfilePicture update(final UUID id, final MemberProfilePicture profilePicture) {
    return profilePicture;
  }

  @Override
  public Optional<MemberProfilePicture> findById(final UUID resourceId) {
    try {
      final MemberProfilePicture profilePicture =
          jdbcTemplate.queryForObject(SELECT_BY_RESOURCE_ID, rowMapper, resourceId);
      return Optional.ofNullable(profilePicture);
    } catch (DataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<MemberProfilePicture> findByMemberId(final Long memberId) {
    try {
      final var profilePicture =
          jdbcTemplate.queryForObject(SELECT_BY_MEMBER_ID, rowMapper, memberId);
      return Optional.ofNullable(profilePicture);
    } catch (DataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public void deleteById(final UUID id) {
    jdbcTemplate.update(DEL_BY_RESOURCE_ID, id);
  }

  @Override
  public void deleteByMemberId(final Long memberId) {
    jdbcTemplate.update(DEL_BY_MEMBER_ID, memberId);
  }

  /** RowMapper for mapping database rows to MentorProfilePicture objects. */
  private static final class MemberProfilePictureRowMapper
      implements RowMapper<MemberProfilePicture> {

    @Override
    public MemberProfilePicture mapRow(final ResultSet rs, final int rowNum) throws SQLException {
      final var resourceId = rs.getString(RESOURCE_ID_COLUMN);

      final Resource resource =
          Resource.builder()
              .id(UUID.fromString(resourceId))
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

      return MemberProfilePicture.builder()
          .memberId(rs.getLong(MEMBER_ID_COLUMN))
          .resourceId(UUID.fromString(resourceId))
          .resource(resource)
          .build();
    }
  }
}
