package com.wcc.platform.domain.platform;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Represents the core team of the community: {@link MemberType#DIRECTOR}, {@link MemberType#LEADER}
 * and {@link MemberType#EVANGELIST}.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor
public class LeadershipMember extends Member {

  @SuppressWarnings("PMD.ImmutableField")
  @JsonIgnore
  private MemberType memberType;

  /** Leadership Builder. */
  @Builder(builderMethodName = "leadershipMemberBuilder")
  @SuppressWarnings("PMD.ExcessiveParameterList")
  public LeadershipMember(
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
    super(
        fullName,
        position,
        email,
        slackDisplayName,
        country,
        city,
        jobTitle,
        companyName,
        memberType,
        images,
        network);

    this.memberType = memberType;
  }
}
