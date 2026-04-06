package com.wcc.platform.repository.postgres;

import com.wcc.platform.domain.auth.PasswordResetToken;
import com.wcc.platform.repository.PasswordResetTokenRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/** PostgreSQL implementation of {@link PasswordResetTokenRepository}. */
@Repository
@RequiredArgsConstructor
public class PostgresPasswordResetTokenRepository implements PasswordResetTokenRepository {

  private static final String SQL_INSERT =
      "INSERT INTO password_reset_tokens (token, user_id, issued_at, expires_at, used)"
          + " VALUES (?, ?, ?, ?, ?)";
  private static final String SQL_SELECT_VALID =
      "SELECT * FROM password_reset_tokens"
          + " WHERE token = ? AND used = FALSE AND expires_at > ?";
  private static final String SQL_MARK_USED =
      "UPDATE password_reset_tokens SET used = TRUE WHERE token = ?";
  private static final String SQL_PURGE_EXPIRED =
      "DELETE FROM password_reset_tokens WHERE expires_at <= ?";

  private static final RowMapper<PasswordResetToken> ROW_MAPPER =
      new RowMapper<>() {
        @Override
        public PasswordResetToken mapRow(final ResultSet rs, final int rowNum) throws SQLException {
          return PasswordResetToken.builder()
              .token(rs.getString("token"))
              .userId(rs.getInt("user_id"))
              .issuedAt(rs.getObject("issued_at", OffsetDateTime.class))
              .expiresAt(rs.getObject("expires_at", OffsetDateTime.class))
              .used(rs.getBoolean("used"))
              .build();
        }
      };

  private final JdbcTemplate jdbc;

  @Override
  public PasswordResetToken create(final PasswordResetToken token) {
    jdbc.update(
        SQL_INSERT,
        token.getToken(),
        token.getUserId(),
        token.getIssuedAt(),
        token.getExpiresAt(),
        token.isUsed());
    return token;
  }

  @Override
  public Optional<PasswordResetToken> findValidByToken(
      final String token, final OffsetDateTime now) {
    return jdbc.query(
        SQL_SELECT_VALID,
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
  public void markUsed(final String token) {
    jdbc.update(SQL_MARK_USED, token);
  }

  @Override
  public void purgeExpired(final OffsetDateTime now) {
    jdbc.update(SQL_PURGE_EXPIRED, now);
  }
}
