package com.wcc.platform.repository.postgres;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.platform.Member;
import com.wcc.platform.domain.platform.MemberType;
import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.repository.MembersRepository;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/** Member data repository */
@Repository
@RequiredArgsConstructor
public class PostgresMemberRepository implements MembersRepository {

  private final JdbcTemplate jdbc;
  private final PostgresCountryRepository countryRepository;
  private final PostgresMemberMemberTypeRepository memberTypeRepo;
  private final PostgresImageRepository imageRepository;
  private final PostgresSocialNetworkRepository socialNetworkRepo;

  @Override
  public Optional<Member> findByEmail(final String email) {
    final String sql = "SELECT * FROM members WHERE email = ?";
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
  public Member create(final Member entity) {
    final String sql =
        "INSERT INTO members (full_name, slack_name, position, company_name, email, city, "
            + "country_id, status_id, bio, years_experience, spoken_language) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NULL, NULL, NULL) RETURNING id";
    final Long countryId =
        countryRepository.findCountryIdByCode(getCountryCode(entity.getCountry()));
    final int defaultStatusId = 1;
    final Long memberId =
        jdbc.queryForObject(
            sql,
            Long.class,
            entity.getFullName(),
            entity.getSlackDisplayName(),
            entity.getPosition(),
            entity.getCompanyName(),
            entity.getEmail(),
            entity.getCity(),
            countryId,
            defaultStatusId);

    // Insert member types
    for (final MemberType type : entity.getMemberTypes()) {
      final Long typeId = memberTypeRepo.findIdByType(type);
      memberTypeRepo.addMemberType(memberId, typeId);
    }

    // Insert images
    for (final Image image : entity.getImages()) {
      imageRepository.addImage(memberId, image);
    }

    // Insert social networks
    if (entity.getNetwork() != null) {
      for (final SocialNetwork network : entity.getNetwork()) {
        socialNetworkRepo.addSocialNetwork(memberId, network);
      }
    }

    return findById(memberId).orElseThrow();
  }

  @Override
  public Member update(final Long id, final Member entity) {
    return null;
  }

  @Override
  public Optional<Member> findById(final Long id) {
    final String sql = "SELECT * FROM members WHERE id = ?";
    return jdbc.query(
        sql,
        rs -> {
          if (rs.next()) {
            return Optional.of(mapRowToMember(rs));
          }
          return Optional.empty();
        },
        id);
  }

  @Override
  public void deleteById(final Long id) {
    // To-do: Implement deletion logic
  }

  /** Mapper method to convert ResultSet to Member object */
  private Member mapRowToMember(final ResultSet rs) throws java.sql.SQLException {
    final Long memberId = rs.getLong("id");
    final Country country = countryRepository.findById(rs.getString("country_code")).orElse(null);
    final List<MemberType> memberTypes = memberTypeRepo.findByMemberId(memberId);
    final List<Image> images = imageRepository.findByMemberId(memberId);
    final List<SocialNetwork> networks = socialNetworkRepo.findByMemberId(memberId);

    return Member.builder()
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

  private String getCountryCode(final Country country) {
    return country != null ? country.countryCode() : null;
  }
}
