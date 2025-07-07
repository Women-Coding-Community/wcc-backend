package com.wcc.platform.repository.postgres;

import com.wcc.platform.domain.platform.Member;
import com.wcc.platform.repository.MembersRepository;
import com.wcc.platform.repository.postgres.component.MemberMapper;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/** Member data repository */
@Repository
@RequiredArgsConstructor
public class PostgresMemberRepository implements MembersRepository {

  private final JdbcTemplate jdbc;
  private final MemberMapper memberMapper;

  @Override
  public Member create(final Member entity) {
    final String sql =
        "INSERT INTO members (full_name, slack_name, position, company_name, email, city, "
            + "country_id, status_id, bio, years_experience, spoken_language) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NULL, NULL, NULL) RETURNING id";
    final Long memberId = memberMapper.addMember(entity, sql);

    return findById(memberId).orElseThrow();
  }

  @Override
  public Optional<Member> findByEmail(final String email) {
    final String sql = "SELECT * FROM members WHERE email = ?";
    return jdbc.query(
        sql,
        rs -> {
          if (rs.next()) {
            return Optional.of(memberMapper.mapRowToMember(rs));
          }
          return Optional.empty();
        },
        email);
  }

  @Override
  public Long findIdByEmail(String email) {
    final String sql = "SELECT id FROM members WHERE email = ?";
    return jdbc.queryForObject(sql, Long.class, email);
  }

  @Override
  public Optional<Member> findById(final Long id) {
    final String sql = "SELECT * FROM members WHERE id = ?";
    return jdbc.query(
        sql,
        rs -> {
          if (rs.next()) {
            return Optional.of(memberMapper.mapRowToMember(rs));
          }
          return Optional.empty();
        },
        id);
  }

  @Override
  public List<Member> getAll() {
    final String sql = "SELECT * FROM members";
    return jdbc.query(sql, (rs, rowNum) -> memberMapper.mapRowToMember(rs));
  }

  @Override
  public Member update(Long id, Member entity) {
    final String sql =
        "UPDATE members SET full_name = ?, slack_name = ?, position = ?, "
            + "company_name = ?, email = ?, city = ?, country_id = ? WHERE id = ?";
    memberMapper.updateMember(entity, sql, id);

    return findById(id).orElseThrow();
  }

  @Override
  public void deleteById(final Long id) {
    // To-do: Implement deletion logic
  }
}
