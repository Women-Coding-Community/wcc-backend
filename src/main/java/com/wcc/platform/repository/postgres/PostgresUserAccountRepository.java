package com.wcc.platform.repository.postgres;

import com.wcc.platform.domain.auth.UserAccount;
import com.wcc.platform.domain.platform.type.RoleType;
import com.wcc.platform.repository.UserAccountRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/** Postgres implementation of the UserAccountRepository interface. */
@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class PostgresUserAccountRepository implements UserAccountRepository {

  private static final String SQL_SELECT_ALL = "SELECT * FROM user_accounts;";
  private static final String SQL_SELECT_BY_ID = "SELECT * FROM user_accounts WHERE id = ?";
  private static final String SQL_SELECT_BY_MAIL = "SELECT * FROM user_accounts WHERE email = ?";
  private static final String SQL_DELETE = "DELETE FROM user_accounts WHERE id = ?";
  private static final String SQL_INSERT =
      "INSERT INTO user_accounts (member_id, email, password_hash) VALUES (?,?,?)";
  private static final String SQL_UPDATE =
      "UPDATE user_accounts SET member_id = ?, email = ?, password_hash = ?, enabled = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
  private static final String SQL_INSERT_ROLE =
      "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)";
  private static final String SQL_SELECT_ROLES = "SELECT role_id FROM user_roles WHERE user_id = ?";
  private static final String SQL_DELETE_ROLES = "DELETE FROM user_roles WHERE user_id = ?";

  private final JdbcTemplate jdbc;

  @Override
  public UserAccount create(final UserAccount userAccount) {
    log.info("Creating userAccount: {}", userAccount);

    jdbc.update(
        SQL_INSERT,
        userAccount.getMemberId(),
        userAccount.getEmail(),
        userAccount.getPasswordHash());

    final var userOption = findByEmail(userAccount.getEmail());
    final var user = userOption.orElseThrow();

    for (final RoleType role : userAccount.getRoles()) {
      jdbc.update(SQL_INSERT_ROLE, user.getId(), role.getTypeId());
    }

    return user;
  }

  @Override
  public UserAccount update(final Integer id, final UserAccount userAccount) {
    jdbc.update(
        SQL_UPDATE,
        userAccount.getMemberId(),
        userAccount.getEmail(),
        userAccount.getPasswordHash(),
        userAccount.isEnabled(),
        id);

    log.debug("Updating userAccount: {}", userAccount);

    jdbc.update(SQL_DELETE_ROLES, id);
    for (final RoleType role : userAccount.getRoles()) {
      jdbc.update(SQL_INSERT_ROLE, id, role.getTypeId());
    }

    userAccount.setId(id);
    return userAccount;
  }

  @Override
  public Optional<UserAccount> findById(final Integer id) {
    final List<UserAccount> users = jdbc.query(SQL_SELECT_BY_ID, (rs, rowNum) -> mapUser(rs), id);
    return users.stream().findFirst();
  }

  @Override
  public Optional<UserAccount> findByEmail(final String email) {
    final List<UserAccount> users =
        jdbc.query(SQL_SELECT_BY_MAIL, (rs, rowNum) -> mapUser(rs), email);
    return users.stream().findFirst();
  }

  @Override
  public void deleteById(final Integer id) {
    jdbc.update(SQL_DELETE_ROLES, id);
    jdbc.update(SQL_DELETE, id);
  }

  @Override
  public List<UserAccount> findAll() {
    return jdbc.query(SQL_SELECT_ALL, (rs, rowNum) -> mapUser(rs));
  }

  private UserAccount mapUser(final ResultSet rs) throws SQLException {
    final Integer userId = rs.getInt("id");
    final List<RoleType> roles =
        jdbc.query(SQL_SELECT_ROLES, (r, i) -> RoleType.fromId(r.getInt("role_id")), userId);

    return UserAccount.builder()
        .id(userId)
        .memberId(rs.getLong("member_id"))
        .email(rs.getString("email"))
        .passwordHash(rs.getString("password_hash"))
        .roles(roles)
        .enabled(rs.getBoolean("enabled"))
        .build();
  }
}
