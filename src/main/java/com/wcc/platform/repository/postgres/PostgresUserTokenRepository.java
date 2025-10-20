package com.wcc.platform.repository.postgres;

import com.wcc.platform.domain.auth.UserToken;
import com.wcc.platform.repository.UserTokenRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/** Repository implementation for managing user tokens using PostgreSQL. */
@Repository
@RequiredArgsConstructor
public class PostgresUserTokenRepository implements UserTokenRepository {
  private static final String SQL_INSERT =
      "INSERT INTO user_tokens (token, user_id, issued_at, expires_at, revoked) VALUES (?,?,?,?,?)";
  private static final String SQL_SELECT =
      "SELECT * FROM user_tokens WHERE token = ? AND revoked = FALSE AND expires_at > ?";

  private static final RowMapper<UserToken> ROW_MAPPER =
      new RowMapper<>() {
        @Override
        public UserToken mapRow(final ResultSet rs, final int rowNum) throws SQLException {
          return UserToken.builder()
              .token(rs.getString("token"))
              .userId(rs.getInt("user_id"))
              .issuedAt(rs.getObject("issued_at", OffsetDateTime.class))
              .expiresAt(rs.getObject("expires_at", OffsetDateTime.class))
              .revoked(rs.getBoolean("revoked"))
              .build();
        }
      };
  private final JdbcTemplate jdbc;

  @Override
  public UserToken create(final UserToken token) {
    jdbc.update(
        SQL_INSERT,
        token.getToken(),
        token.getUserId(),
        token.getIssuedAt(),
        token.getExpiresAt(),
        token.isRevoked());
    return token;
  }

  @Override
  public Optional<UserToken> findValidByToken(final String token, final OffsetDateTime now) {
    return jdbc.query(
        SQL_SELECT,
        rs -> {
          if (rs.next()) {
            return Optional.ofNullable(ROW_MAPPER.mapRow(rs, 0));
          }
          return Optional.empty();
        },
        token,
        now);
  }

  @Override
  public void revoke(final String token) {
    jdbc.update("UPDATE user_tokens SET revoked = TRUE WHERE token = ?", token);
  }

  @Override
  public void purgeExpired(final OffsetDateTime now) {
    jdbc.update("DELETE FROM user_tokens WHERE expires_at <= ?", now);
  }
}
