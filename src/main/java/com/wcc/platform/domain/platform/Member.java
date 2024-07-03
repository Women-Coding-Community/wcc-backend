package com.wcc.platform.domain.platform;

import com.wcc.platform.domain.cms.attributes.Image;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Member Domain class with all attributes for all types of members. */
@Data
@NoArgsConstructor
public class Member {

  // Use builder
  private String fullName;
  private String position;
  //    private String email;
  //    private Country country;
  //    private String city;
  //    private String jobTitle;
  //    private String companyName;
  private MemberType memberType;
  private List<Image> images;
  private List<SocialNetwork> network;
}
