package com.wcc.platform.repository.postgres.component;

import com.wcc.platform.domain.platform.mentorship.Mentor;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MentorMapper {

  private final JdbcTemplate jdbc;

  public Mentor mapRowToMentor(final ResultSet resultSet) throws SQLException {
    return null;
  }
}
