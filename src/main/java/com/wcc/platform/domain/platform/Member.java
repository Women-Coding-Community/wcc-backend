package com.wcc.platform.domain.platform;

import com.wcc.platform.domain.cms.attributes.Image;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Member Domain class with all attributes for all types of members. */
@Data
@NoArgsConstructor
public class Member {
  private String fullName;
  private String position;
  private MemberType memberType;
  private List<Image> images;
  private List<SocialNetwork> network;
}
