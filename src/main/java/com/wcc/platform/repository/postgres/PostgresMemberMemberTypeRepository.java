package com.wcc.platform.repository.postgres;

import com.wcc.platform.domain.platform.MemberType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/** Member to member type mapping repository */
@Repository
@RequiredArgsConstructor
public class PostgresMemberMemberTypeRepository {
  private final JdbcTemplate jdbc;

  /** Retrieves a list of member type associated with the specified member. */
  public List<MemberType> findByMemberId(final Long memberId) {
    final String sql =
        " SELECT mt.name FROM member_member_types mmt JOIN member_types mt "
            + "ON mmt.member_type_id = mt.id WHERE mmt.member_id = ?";
    return jdbc.query(sql, (rs, rowNum) -> MemberType.valueOf(rs.getString("name")), memberId);
  }

  /** Retrieves member type id associated with the specified member type. */
  public Long findIdByType(final MemberType type) {
    final String sql = "SELECT id FROM member_types WHERE name = ?";
    return jdbc.queryForObject(sql, Long.class, type.name());
  }

  /** Add a member type to a member. */
  public void addMemberType(final Long memberId, final Long memberTypeId) {
    final String sql = "INSERT INTO member_member_types (member_id, member_type_id) VALUES (?, ?)";
    jdbc.update(sql, memberId, memberTypeId);
  }

  /** Remove a member type from a member. */
  public void removeMemberType(final Long memberId, final Long memberTypeId) {
    final String sql = "DELETE FROM member_member_types WHERE member_id = ? AND member_type_id = ?";
    jdbc.update(sql, memberId, memberTypeId);
  }

  /** Deletes all member types associated with a specific member. */
  public void deleteByMemberId(final Long memberId) {
    final String sql = "DELETE FROM member_member_types WHERE member_id = ?";
    jdbc.update(sql, memberId);
  }
}
