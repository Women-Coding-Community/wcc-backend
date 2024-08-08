package com.wcc.platform.domain.platform;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents the core team of the community: {@link MemberType#DIRECTOR}, {@link MemberType#LEADER}
 * and {@link MemberType#EVANGELIST}.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class LeadershipMember extends Member {

  @JsonIgnore private MemberType memberType;

  /** Leadership Builder. */
  @Builder(builderMethodName = "leadershipMemberBuilder")
  public LeadershipMember(
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

    this.memberType = memberType;
  }

  /** Necessary constructor for jackson. */
  public LeadershipMember() {
    super();
  }
}
