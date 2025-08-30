package com.wcc.platform.repository.postgres.component;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.type.MemberType;
import com.wcc.platform.repository.postgres.PostgresCountryRepository;
import com.wcc.platform.repository.postgres.PostgresMemberImageRepository;
import com.wcc.platform.repository.postgres.PostgresMemberMemberTypeRepository;
import com.wcc.platform.repository.postgres.PostgresSocialNetworkRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberMapper {

  private final JdbcTemplate jdbc;
  private final PostgresCountryRepository countryRepository;
  private final PostgresMemberMemberTypeRepository memberTypeRepo;
  private final PostgresMemberImageRepository imageRepository;
  private final PostgresSocialNetworkRepository socialNetworkRepo;

  /** Mapper method to convert ResultSet to Member object */
  public Member mapRowToMember(final ResultSet rs) throws SQLException {
    final Long memberId = rs.getLong("id");
    final Country country = countryRepository.findById(rs.getLong("country_id")).orElse(null);
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

  /** Adds a new member to the database and returns the member ID */
  public Long addMember(final Member member, final String sql) {
    final int defaultStatusId = 1;
    final Long memberId =
        jdbc.queryForObject(
            sql,
            Long.class,
            member.getFullName(),
            member.getSlackDisplayName(),
            member.getPosition(),
            member.getCompanyName(),
            member.getEmail(),
            member.getCity(),
            getCountryId(member.getCountry()),
            defaultStatusId);
    addMemberImages(memberId, member);
    addMemberTypes(memberId, member);
    addSocialNetworks(memberId, member);

    return memberId;
  }

  /** Updates an existing member in the database */
  public void updateMember(final Member member, final String sql, final Long memberId) {
    jdbc.update(
        sql,
        member.getFullName(),
        member.getSlackDisplayName(),
        member.getPosition(),
        member.getCompanyName(),
        member.getEmail(),
        member.getCity(),
        getCountryId(member.getCountry()),
        memberId);

    // Update member types
    memberTypeRepo.deleteByMemberId(memberId);
    addMemberTypes(memberId, member);

    // Update images
    imageRepository.deleteMemberImage(memberId);
    addMemberImages(memberId, member);

    // Update social networks
    socialNetworkRepo.deleteByMemberId(memberId);
    addSocialNetworks(memberId, member);
  }

  /** Adds images to the member */
  private void addMemberImages(final Long memberId, final Member member) {
    if (member.getImages() != null) {
      member.getImages().forEach(image -> imageRepository.addMemberImage(memberId, image));
    }
  }

  /** Adds member types to the member */
  private void addMemberTypes(final Long memberId, final Member member) {
    if (member.getMemberTypes() != null) {
      member
          .getMemberTypes()
          .forEach(
              type -> {
                final Long typeId = memberTypeRepo.findMemberTypeId(type);
                memberTypeRepo.addMemberType(memberId, typeId);
              });
    }
  }

  /** Adds social networks to the member */
  private void addSocialNetworks(final Long memberId, final Member member) {
    if (member.getNetwork() != null) {
      member
          .getNetwork()
          .forEach(socialNetwork -> socialNetworkRepo.addSocialNetwork(memberId, socialNetwork));
    }
  }

  /** Retrieves the country ID based on the provided country or defaults to "GB" */
  private Long getCountryId(final Country country) {
    return countryRepository.findCountryIdByCode(country != null ? country.countryCode() : "GB");
  }
}
