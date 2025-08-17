package com.wcc.platform.repository.postgres;

import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.attributes.ImageType;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/** Image data repository */
@Repository
@RequiredArgsConstructor
public class PostgresMemberImageRepository {
  private final JdbcTemplate jdbc;

  /** Retrieves a list of images associated with the specified member ID. */
  public List<Image> findByMemberId(final Long memberId) {
    final String sql =
        "SELECT mi.image_url, mi.image_alt, it.type "
            + "FROM member_images mi JOIN image_types it "
            + "ON mi.image_type_id = it.id WHERE mi.member_id = ?";

    return jdbc.query(
        sql,
        (rs, rowNum) ->
            new Image(
                rs.getString("image_url"),
                rs.getString("image_alt"),
                ImageType.valueOf(rs.getString("type").toUpperCase(Locale.ENGLISH))),
        memberId);
  }

  /** Add the image for a specific member */
  public void addMemberImage(final Long memberId, final Image image) {
    final String sql =
        "INSERT INTO member_images (member_id, image_url, image_alt, image_type_id) "
            + "VALUES (?, ?, ?, (SELECT id FROM image_types WHERE type = ?))";
    jdbc.update(
        sql, memberId, image.path(), image.alt(), image.type().name().toLowerCase(Locale.ENGLISH));
  }

  /** Deletes an image associated with a specific member. */
  public void deleteMemberImage(final Long memberId) {
    final String sql = "DELETE FROM member_images WHERE member_id = ?";
    jdbc.update(sql, memberId);
  }
}
