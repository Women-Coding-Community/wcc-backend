package com.wcc.platform.repository.postgres;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.repository.CrudRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostgresCountryRepository implements CrudRepository<Country, String> {

  private final JdbcTemplate jdbc;

  @Override
  public Country create(Country entity) {
    return null;
  }

  @Override
  public Country update(String id, Country entity) {
    return null;
  }

  @Override
  public Optional<Country> findById(String countryCode) {
    String sql = "SELECT * FROM countries WHERE country_code = ?";
    return jdbc.query(
        sql,
        rs -> {
          if (rs.next()) {
            return Optional.of(
                new Country(rs.getString("country_code"), rs.getString("country_name")));
          }
          return Optional.empty();
        },
        countryCode);
  }

  @Override
  public void deleteById(String countryCode) {
    jdbc.update("DELETE FROM countries WHERE country_code = ?", countryCode);
  }
}
