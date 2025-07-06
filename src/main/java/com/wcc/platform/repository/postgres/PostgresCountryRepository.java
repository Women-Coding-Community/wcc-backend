package com.wcc.platform.repository.postgres;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.repository.CrudRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/** Country data repository */
@Repository
@RequiredArgsConstructor
public class PostgresCountryRepository implements CrudRepository<Country, String> {

  private final JdbcTemplate jdbc;

  @Override
  public Country create(final Country entity) {
    final String sql = "INSERT INTO countries (country_code, country_name) VALUES (?, ?)";
    jdbc.update(sql, entity.countryCode(), entity.countryName());
    return entity;
  }

  @Override
  public Country update(final String countryCode, final Country entity) {
    final String sql = "UPDATE countries SET country_name = ? WHERE country_code = ?";
    jdbc.update(sql, entity.countryName(), countryCode);
    return entity;
  }

  @Override
  public Optional<Country> findById(final String countryCode) {
    final String sql = "SELECT * FROM countries WHERE country_code = ?";
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
  public void deleteById(final String countryCode) {
    jdbc.update("DELETE FROM countries WHERE country_code = ?", countryCode);
  }

  /** Retrieves the country ID associated with the specified country code. */
  public Long findCountryIdByCode(final String countryCode) {
    final String sql = "SELECT id FROM countries WHERE country_code = ?";
    return jdbc.queryForObject(sql, Long.class, countryCode);
  }
}
