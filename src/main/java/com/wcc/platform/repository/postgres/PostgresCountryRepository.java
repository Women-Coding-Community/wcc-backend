package com.wcc.platform.repository.postgres;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.repository.CrudRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

/** Country data repository. */
@Repository
@RequiredArgsConstructor
public class PostgresCountryRepository implements CrudRepository<Country, Long> {

  private final JdbcTemplate jdbc;

  @Override
  public Country create(final Country entity) {
    final String sql = "INSERT INTO countries (country_code, country_name) VALUES (?, ?)";
    jdbc.update(sql, entity.countryCode(), entity.countryName());
    return entity;
  }

  @Override
  public Country update(final Long id, final Country entity) {
    final String sql = "UPDATE countries SET country_name = ? AND country_code = ? WHERE id = ?";
    jdbc.update(sql, entity.countryName(), entity.countryCode(), id);
    return entity;
  }

  @Override
  public Optional<Country> findById(final Long id) {
    final String sql = "SELECT * FROM countries WHERE id = ?";
    return jdbc.query(
        sql,
        rs -> {
          if (rs.next()) {
            return Optional.of(
                new Country(rs.getString("country_code"), rs.getString("country_name")));
          }
          return Optional.empty();
        },
        id);
  }

  @Override
  public void deleteById(final Long id) {
    jdbc.update("DELETE FROM countries WHERE id = ?", id);
  }

  /** Retrieves the country ID associated with the specified country code. */
  public Long findCountryIdByCode(final String countryCode) {
    final String sql = "SELECT id FROM countries WHERE country_code = ?";
    return jdbc.queryForObject(sql, SingleColumnRowMapper.newInstance(Long.class), countryCode);
  }
}
