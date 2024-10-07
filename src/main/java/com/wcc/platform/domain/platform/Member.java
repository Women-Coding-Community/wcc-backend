package com.wcc.platform.domain.platform;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** Member class with common attributes for all community members. */
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Data
@Builder(toBuilder = true)
public class Member {

  private String fullName;
  private String position;
  private String email;
  private String slackDisplayName;
  private Country country;
  private String city;
  private String jobTitle;
  private String companyName;
  private List<MemberType> memberTypes;
  private List<Image> images;
  private List<SocialNetwork> network;
}
