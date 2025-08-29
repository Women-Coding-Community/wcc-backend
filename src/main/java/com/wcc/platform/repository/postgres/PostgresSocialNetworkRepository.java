package com.wcc.platform.repository.postgres;

import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.domain.platform.SocialNetworkType;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/** SocialNetwork and member mapping data repository. */
@Repository
@RequiredArgsConstructor
public class PostgresSocialNetworkRepository {
  private final JdbcTemplate jdbc;

  /** Retrieves list of social network links for a member. */
  public List<SocialNetwork> findByMemberId(final Long memberId) {
    final String sql =
        """
        SELECT msn.link, snt.type
        FROM member_social_networks msn
        JOIN social_network_types snt ON msn.network_type_id = snt.id
        WHERE msn.member_id = ?
        """;
    return jdbc.query(
        sql,
        (rs, rowNum) ->
            new SocialNetwork(
                SocialNetworkType.valueOf(rs.getString("type").toUpperCase(Locale.ENGLISH)),
                rs.getString("link")),
        memberId);
  }

  /** Add a social network link for a member */
  public void addSocialNetwork(final Long memberId, final SocialNetwork network) {
    final String sql =
        """
        INSERT INTO member_social_networks (member_id, link, network_type_id)
        VALUES (?, ?, (SELECT id FROM social_network_types WHERE type = ?))
        """;
    jdbc.update(sql, memberId, network.link(), network.type().name().toUpperCase(Locale.ENGLISH));
  }

  /** Deletes all social network links associated with a specific member. */
  public void deleteByMemberId(final Long memberId) {
    final String sql = "DELETE FROM member_social_networks WHERE member_id = ?";
    jdbc.update(sql, memberId);
  }
}
