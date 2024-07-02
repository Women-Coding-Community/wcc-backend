package com.wcc.platform.domain.platform;

import com.wcc.platform.domain.cms.attributes.Image;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

<<<<<<< HEAD
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
=======
@Data
@NoArgsConstructor
public class Member {

    private String fullName;
    private String position;
    private MemberType memberType;
    private List<Image> images;
    private List<SocialNetwork> network;
}
>>>>>>> 99dacc76e5849b960f2eae2a18c8c44e2edc091e
