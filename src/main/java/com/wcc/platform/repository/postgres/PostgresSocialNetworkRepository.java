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

  private static final String INSERT_MEMBER_SOCIAL_NETWORK =
      "INSERT INTO member_social_networks (member_id, link, network_type_id) VALUES (?, ?, ?) ";
  private static final String SELECT_BY_MEMBER =
      """
        SELECT msn.link, snt.type
        FROM member_social_networks msn
        JOIN social_network_types snt ON msn.network_type_id = snt.id
        WHERE msn.member_id = ?
        """;
  private static final String DELETE_SOCIAL_NETWORK =
      "DELETE FROM member_social_networks WHERE member_id = ?";

  private final JdbcTemplate jdbc;

  /** Retrieves list of social network for a member. */
  public List<SocialNetwork> findByMemberId(final Long memberId) {
    return jdbc.query(
        SELECT_BY_MEMBER,
        (rs, rowNum) ->
            new SocialNetwork(
                SocialNetworkType.valueOf(rs.getString("type").toUpperCase(Locale.ENGLISH)),
                rs.getString("link")),
        memberId);
  }

  /** Add a social network link for a member. */
  public void addSocialNetwork(final Long memberId, final SocialNetwork network) {
    jdbc.update(INSERT_MEMBER_SOCIAL_NETWORK, memberId, network.link(), network.type().getTypeId());
  }

  /** Deletes all social network links associated with a specific member. */
  public void deleteByMemberId(final Long memberId) {
    jdbc.update(DELETE_SOCIAL_NETWORK, memberId);
  }
}
