package com.wcc.platform.repository.postgres;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.platform.Member;
import com.wcc.platform.domain.platform.MemberType;
import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.repository.MembersRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostgresMemberRepository implements MembersRepository {

  private final JdbcTemplate jdbc;

  @Override
  public Optional<Member> findByEmail(String email) {
    String sql = "SELECT * FROM members WHERE email = ?";
    return jdbc.query(
        sql,
        rs -> {
          if (rs.next()) {
            return Optional.of(mapRowToMember(rs));
          }
          return Optional.empty();
        },
        email);
  }

  @Override
  public Member create(Member entity) {
    return null;
  }

  @Override
  public Member update(Long id, Member entity) {
    return null;
  }

  @Override
  public Optional<Member> findById(Long id) {
    return Optional.empty();
  }

  @Override
  public void deleteById(Long id) {}

  private Member mapRowToMember(java.sql.ResultSet rs) throws java.sql.SQLException {
    Long memberId = rs.getLong("id");
    Country country = countryRepository.findById(rs.getInt("country_id")).orElse(null);
    List<MemberType> memberTypes = memberTypeRepository.findByMemberId(memberId);
    List<Image> images = imageRepository.findByMemberId(memberId);
    List<SocialNetwork> networks = socialNetworkRepository.findByMemberId(memberId);

    return Member.builder()
        .id(memberId)
        .fullName(rs.getString("full_name"))
        .position(rs.getString("position"))
        .email(rs.getString("email"))
        .slackDisplayName(rs.getString("slack_name"))
        .country(country)
        .city(rs.getString("city"))
        .companyName(rs.getString("company_name"))
        .memberTypes(memberTypes)
        .images(images)
        .network(networks)
        .build();
  }
}
