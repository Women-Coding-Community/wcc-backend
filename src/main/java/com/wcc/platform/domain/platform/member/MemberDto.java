package com.wcc.platform.domain.platform.member;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.domain.platform.type.MemberType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** MemberDto class with common attributes for all community members. */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class MemberDto {
  private Long id;
  private String fullName;
  private String position;
  private String email;
  private String slackDisplayName;
  private Country country;
  private String city;
  private String companyName;
  private List<MemberType> memberTypes;
  private List<Image> images;
  private List<SocialNetwork> network;

  /**
   * Update member using attributes from his DTO.
   *
   * @param member member to be updated
   * @return Updated member
   */
  public Member merge(final Member member) {
    return member.toBuilder()
        .id(member.getId())
        .fullName(getFullName())
        .position(getPosition())
        .email(getEmail())
        .slackDisplayName(getSlackDisplayName())
        .country(getCountry())
        .city(getCity())
        .companyName(getCompanyName())
        .memberTypes(getMemberTypes())
        .images(getImages())
        .network(getNetwork())
        .build();
  }
}
