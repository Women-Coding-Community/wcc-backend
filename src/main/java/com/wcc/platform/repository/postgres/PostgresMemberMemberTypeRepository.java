package com.wcc.platform.repository.postgres;

import com.wcc.platform.domain.platform.type.MemberType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/** Member-to-member type mapping repository. */
@Repository
@RequiredArgsConstructor
public class PostgresMemberMemberTypeRepository {
  private final JdbcTemplate jdbc;

  /** Retrieves a list of a member type associated with the specified member. */
  public List<MemberType> findByMemberId(final Long memberId) {
    final String sql = "SELECT member_type_id FROM member_member_types WHERE member_id = ?";
    return jdbc.query(
        sql, (rs, rowNum) -> MemberType.fromId(rs.getInt("member_type_id")), memberId);
  }

  /** Add a member type to a member. */
  public void addMemberType(final Long memberId, final int memberTypeId) {
    final String sql = "INSERT INTO member_member_types (member_id, member_type_id) VALUES (?, ?)";
    jdbc.update(sql, memberId, memberTypeId);
  }

  /** Deletes all member types associated with a specific member. */
  public void deleteByMemberId(final Long memberId) {
    final String sql = "DELETE FROM member_member_types WHERE member_id = ?";
    jdbc.update(sql, memberId);
  }
}
