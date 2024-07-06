package com.wcc.platform.domain.platform;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represents the core team of the community: {@link MemberType#DIRECTOR}, {@link MemberType#LEADER}
 * and {@link MemberType#EVANGELIST}.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LeadershipMember extends Member {

  @JsonIgnore private MemberType memberType;

  /** Leadership Builder. */
  @Builder(builderMethodName = "leadershipMemberBuilder")
  public LeadershipMember(
      String fullName,
      String position,
      String email,
      Country country,
      String city,
      String jobTitle,
      String companyName,
      MemberType memberType,
      List<Image> images,
      List<SocialNetwork> network) {
    super(
        fullName,
        position,
        email,
        country,
        city,
        jobTitle,
        companyName,
        memberType,
        images,
        network);
  }

  public LeadershipMember() {}
}
