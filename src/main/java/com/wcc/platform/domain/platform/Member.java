package com.wcc.platform.domain.platform;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** Member class with all attributes for all types of members. */
// @Getter
// @AllArgsConstructor
// @NoArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
@Data

public class Member {

  private String fullName;
  private String position;
  private String email;
  private String slackDisplayName;
  private Country country;
  private String city;
  private String jobTitle;
  private String companyName;
  private MemberType memberType;
  private List<Image> images;
  private List<SocialNetwork> network;

  /** Member Builder. */
  public Member(
      final String fullName,
      final String position,
      final String email,
      final String slackDisplayName,
      final Country country,
      final String city,
      final String jobTitle,
      final String companyName,
      final MemberType memberType,
      final List<Image> images,
      final List<SocialNetwork> network) {
    this.fullName = fullName;
    this.position = position;
    this.email = email;
    this.slackDisplayName = slackDisplayName;
    this.country = country;
    this.city = city;
    this.jobTitle = jobTitle;
    this.companyName = companyName;
    this.memberType = memberType;
    this.images = images;
    this.network = network;
  }

  public Member() {
    // Necessary constructor for jackson.
  }
}
