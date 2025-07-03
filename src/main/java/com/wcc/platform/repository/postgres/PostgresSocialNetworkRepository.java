package com.wcc.platform.repository.postgres;

import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.domain.platform.SocialNetworkType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/* SocialNetwork and member mapping data repository*/

@Repository
@RequiredArgsConstructor
public class PostgresSocialNetworkRepository {
  private final JdbcTemplate jdbc;

  /*Retrieve list of social network links for a member */
  public List<SocialNetwork> findByMemberId(final Long memberId) {
    String sql =
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
                SocialNetworkType.valueOf(rs.getString("type").toUpperCase()),
                rs.getString("link")),
        memberId);
  }
}
