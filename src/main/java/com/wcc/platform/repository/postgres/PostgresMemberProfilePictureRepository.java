package com.wcc.platform.repository.postgres;

import com.wcc.platform.domain.resource.MemberProfilePicture;
import com.wcc.platform.repository.MemberProfilePictureRepository;
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
  private final RowMapper<MemberProfilePicture> rowMapper = new MemberProfilePictureRowMapper();

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
}
