package com.wcc.platform.repository.postgres;

import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.postgres.component.MemberMapper;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

/**
 * Implementation of the MembersRepository interface for managing Member entities using PostgreSQL
 * as the data source. This class interacts with the database using SQL queries and maps the result
 * sets to Member objects with the help of MemberMapper.
 */
@Repository
@Primary
@RequiredArgsConstructor
public class PostgresMemberRepository implements MemberRepository {

  public static final String MEMBER_ID_COLUMN = "member_id";

  private static final String DELETE_SQL = "DELETE FROM members WHERE id = ?";
  private static final String DELETE_BY_SQL = "DELETE FROM members WHERE email = ?";
  private static final String SELECT_BY_EMAIL = "SELECT * FROM members WHERE email = ?";
  private static final String SELECT_BY_ID = "SELECT * FROM members WHERE id = ?";
  private static final String SELECT_ALL_MEMBERS = "SELECT * FROM members";

  private final JdbcTemplate jdbc;
  private final MemberMapper memberMapper;

  @Override
  public Member create(final Member entity) {
    final Long memberId = memberMapper.addMember(entity);

    return findById(memberId).orElseThrow();
  }

  @Override
  public Optional<Member> findByEmail(final String email) {
    return jdbc.query(
        SELECT_BY_EMAIL,
        rs -> {
          if (rs.next()) {
            return Optional.of(memberMapper.mapRowToMember(rs));
          }
          return Optional.empty();
        },
        email);
  }

  @Override
  public Long findIdByEmail(final String email) {
    return jdbc.queryForObject(
        SELECT_BY_EMAIL, SingleColumnRowMapper.newInstance(Long.class), email);
  }

  @Override
  public Optional<Member> findById(final Long id) {
    return jdbc.query(
        SELECT_BY_ID,
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
    return jdbc.query(SELECT_ALL_MEMBERS, (rs, rowNum) -> memberMapper.mapRowToMember(rs));
  }

  @Override
  public Member update(final Long id, final Member member) {
    memberMapper.updateMember(member, id);

    return member;
  }

  @Override
  public void deleteById(final Long memberId) {
    jdbc.update(DELETE_SQL, memberId);
  }

  @Override
  public void deleteByEmail(final String email) {
    jdbc.update(DELETE_BY_SQL, email);
  }
}
