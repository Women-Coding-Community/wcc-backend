package com.wcc.platform.repository.postgres;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.cms.attributes.HeroSection;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.attributes.style.CustomStyle;
import com.wcc.platform.repository.HeroSectionRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class PostgresHeroSectionRepository implements HeroSectionRepository {
  private static final String SQL_GET_RECORD_BY_NAME =
      "SELECT title, subtitle, images, custom_style FROM hero_sections WHERE page_id = ?";
  private static final String SQL_INSERT_RECORD =
      "INSERT INTO hero_sections "
          + "(page_id, title, subtitle, images, custom_style) VALUES (?, ?, ?, ?, ?)";
  private static final String SQL_UPDATE_RECORD =
      "UPDATE hero_sections SET title = ?, subtitle = ?, images = ?, custom_style = ?";
  private final JdbcTemplate jdbc;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public Optional<HeroSection> findByPage(final String pageId) {
    return jdbc.query(SQL_GET_RECORD_BY_NAME, (rs, rowNum) -> mapRow(rs), pageId).stream()
        .findFirst();
  }

  @Override
  public void save(final String pageId, final HeroSection heroSection) {
    jdbc.update(
        SQL_INSERT_RECORD,
        pageId,
        heroSection.title(),
        heroSection.subtitle(),
        toJson(heroSection.images()),
        toJson(heroSection.customStyle()));
  }

  @Override
  public void update(String pageId, HeroSection heroSection) {
    jdbc.update(
        SQL_UPDATE_RECORD,
        heroSection.title(),
        heroSection.subtitle(),
        toJson(heroSection.images()),
        toJson(heroSection.customStyle()),
        pageId);
  }

  private HeroSection mapRow(ResultSet rs) throws SQLException {
    try {
      List<Image> images =
          rs.getString("images") == null
              ? List.of()
              : objectMapper.readValue(rs.getString("images"), new TypeReference<List<Image>>() {});
      CustomStyle customStyle =
          objectMapper.readValue(rs.getString("custom_style"), CustomStyle.class);
      return new HeroSection(rs.getString("title"), rs.getString("subtitle"), images, customStyle);
    } catch (Exception e) {
      throw new SQLException("Failed to map HeroSection JSON", e);
    }
  }

  private String toJson(Object obj) {
    try {
      return objectMapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to serialize JSON", e);
    }
  }
}
