package com.wcc.platform.repository.postgres;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

/**
 * Unit tests for PostgresCountryRepository.
 *
 * <p>Tests all CRUD operations and custom query methods for the Country entity.
 */
class PostgresCountryRepositoryTest {

  private JdbcTemplate jdbc;
  private PostgresCountryRepository repository;

  @BeforeEach
  void setUp() {
    jdbc = mock(JdbcTemplate.class);
    repository = new PostgresCountryRepository(jdbc);
  }

  @Test
  @DisplayName("Given valid country, when creating, then it should insert and return the country")
  void shouldCreateCountry() {
    Country country = new Country("US", "United States");
    when(jdbc.update(anyString(), eq("US"), eq("United States"))).thenReturn(1);

    Country result = repository.create(country);

    assertThat(result).isNotNull();
    assertThat(result.countryCode()).isEqualTo("US");
    assertThat(result.countryName()).isEqualTo("United States");
    verify(jdbc)
        .update(
            "INSERT INTO countries (country_code, country_name) VALUES (?, ?)",
            "US",
            "United States");
  }

  @Test
  @DisplayName(
      "Given existing country ID, when updating Then it should update and return the country")
  void shouldUpdateCountry() {
    Long countryId = 1L;
    Country updatedCountry = new Country("UK", "United Kingdom");
    when(jdbc.update(anyString(), eq("United Kingdom"), eq("UK"), eq(countryId))).thenReturn(1);

    Country result = repository.update(countryId, updatedCountry);

    assertThat(result).isNotNull();
    assertThat(result.countryCode()).isEqualTo("UK");
    assertThat(result.countryName()).isEqualTo("United Kingdom");
    verify(jdbc)
        .update(
            "UPDATE countries SET country_name = ? AND country_code = ? WHERE id = ?",
            "United Kingdom",
            "UK",
            countryId);
  }

  @Test
  @DisplayName("Given existing country ID, when finding by ID, then it should return the country")
  void shouldFindCountryById() {
    Long countryId = 1L;
    Country expectedCountry = new Country("CA", "Canada");

    when(jdbc.query(anyString(), ArgumentMatchers.<ResultSetExtractor<Object>>any(), eq(countryId)))
        .thenReturn(Optional.of(expectedCountry));

    Optional<Country> result = repository.findById(countryId);

    assertThat(result).isPresent();
    assertThat(result.get().countryCode()).isEqualTo("CA");
    assertThat(result.get().countryName()).isEqualTo("Canada");
    verify(jdbc)
        .query(
            eq("SELECT * FROM countries WHERE id = ?"),
            ArgumentMatchers.<ResultSetExtractor<Object>>any(),
            eq(countryId));
  }

  @Test
  @DisplayName("Given non-existent country ID, when finding by ID, then it should return empty")
  void shouldReturnEmptyWhenCountryNotFoundById() {
    Long countryId = 999L;

    when(jdbc.query(anyString(), ArgumentMatchers.<ResultSetExtractor<Object>>any(), eq(countryId)))
        .thenReturn(Optional.empty());

    Optional<Country> result = repository.findById(countryId);

    assertThat(result).isEmpty();
    verify(jdbc)
        .query(
            eq("SELECT * FROM countries WHERE id = ?"),
            ArgumentMatchers.<ResultSetExtractor<Object>>any(),
            eq(countryId));
  }

  @Test
  @DisplayName("Given existing country ID, when deleting, then it should delete the country")
  void shouldDeleteCountryById() {
    Long countryId = 1L;
    when(jdbc.update(anyString(), eq(countryId))).thenReturn(1);

    repository.deleteById(countryId);

    verify(jdbc).update("DELETE FROM countries WHERE id = ?", countryId);
  }

  @Test
  @DisplayName("Given valid country code, when finding country ID, then it should return the ID")
  void shouldFindCountryIdByCode() {
    String countryCode = "BR";
    Long expectedId = 5L;

    when(jdbc.query(
            anyString(), ArgumentMatchers.<ResultSetExtractor<Object>>any(), eq(countryCode)))
        .thenReturn(expectedId);

    Long result = repository.findCountryIdByCode(countryCode);

    assertThat(result).isEqualTo(expectedId);
    verify(jdbc)
        .query(
            eq("SELECT id FROM countries WHERE country_code = ?"),
            ArgumentMatchers.<ResultSetExtractor<Object>>any(),
            eq(countryCode));
  }

  @Test
  @DisplayName(
      "Given non-existent country code, when finding country ID, "
          + "then it should throw ContentNotFoundException")
  void shouldThrowExceptionWhenCountryCodeNotFound() throws SQLException {
    String countryCode = "XX";
    ResultSet resultSet = mock(ResultSet.class);
    when(resultSet.next()).thenReturn(false);

    doAnswer(
            invocation -> {
              ResultSetExtractor<Long> extractor = invocation.getArgument(1);
              return extractor.extractData(resultSet);
            })
        .when(jdbc)
        .query(anyString(), ArgumentMatchers.<ResultSetExtractor<Long>>any(), eq(countryCode));

    var exception =
        assertThrows(
            ContentNotFoundException.class, () -> repository.findCountryIdByCode(countryCode));

    assertThat(exception.getMessage()).isEqualTo("Country code not found: XX");
    verify(jdbc)
        .query(
            eq("SELECT id FROM countries WHERE country_code = ?"),
            ArgumentMatchers.<ResultSetExtractor<Long>>any(),
            eq(countryCode));
  }
}
