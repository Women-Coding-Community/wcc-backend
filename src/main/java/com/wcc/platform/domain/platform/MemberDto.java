package com.wcc.platform.domain.platform;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import java.util.List;
import lombok.Getter;

/** MemberDto class with common attributes for all community members. */
// TODO: Change class to record
@Getter
public class MemberDto {

  private String fullName;
  private String position;
  private String slackDisplayName;
  private Country country;
  private String city;
  private String jobTitle;
  private String companyName;
  private List<SocialNetwork> network;
  private List<Image> images;
}
