package com.wcc.platform.domain.platform;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/** Member Domain class with all attributes for all types of members. */
@Data
@Builder
@AllArgsConstructor
public class Member {
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

  public Member() {}
}
