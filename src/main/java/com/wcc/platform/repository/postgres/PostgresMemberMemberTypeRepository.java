package com.wcc.platform.repository.postgres;

import com.wcc.platform.domain.platform.MemberType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostgresMemberMemberTypeRepository {
  private final JdbcTemplate jdbc;

  public List<MemberType> findByMemberId(Long memberId) {
    String sql =
        " SELECT mt.name FROM member_member_types mmt JOIN member_types mt "
            + "ON mmt.member_type_id = mt.id WHERE mmt.member_id = ?";
    return jdbc.query(sql, (rs, rowNum) -> MemberType.valueOf(rs.getString("name")), memberId);
  }

  public void addMemberType(Long memberId, Long memberTypeId) {
    String sql = "INSERT INTO member_member_types (member_id, member_type_id) VALUES (?, ?)";
    jdbc.update(sql, memberId, memberTypeId);
  }

  public void removeMemberType(Long memberId, Long memberTypeId) {
    String sql = "DELETE FROM member_member_types WHERE member_id = ? AND member_type_id = ?";
    jdbc.update(sql, memberId, memberTypeId);
  }
}
