package com.wcc.platform.domain.platform;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import java.util.List;
import lombok.Builder;
import lombok.Data;

/** Member Domain class with all attributes for all types of members. */
@Data
@Builder
public class Member {

  // Use builder
  private String fullName;
  private String position;
  private String email;
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
