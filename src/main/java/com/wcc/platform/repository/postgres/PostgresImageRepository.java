package com.wcc.platform.repository.postgres;

import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.attributes.ImageType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/* Image data repository */
@Repository
@RequiredArgsConstructor
public class PostgresImageRepository {
  private final JdbcTemplate jdbc;

  /** Retrieves a list of images associated with the specified member ID. */
  public List<Image> findByMemberId(final Long memberId) {
    String sql =
        "SELECT mi.image_url, mi.image_alt, it.type "
            + "FROM member_images mi JOIN image_types it "
            + "ON mi.image_type_id = it.id WHERE mi.member_id = ?";

    return jdbc.query(
        sql,
        (rs, rowNum) ->
            new Image(
                rs.getString("image_url"),
                rs.getString("image_alt"),
                ImageType.valueOf(rs.getString("type").toUpperCase())),
        memberId);
  }
}
