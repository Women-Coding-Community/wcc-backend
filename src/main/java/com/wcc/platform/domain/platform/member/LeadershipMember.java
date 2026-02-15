package com.wcc.platform.domain.platform.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.domain.platform.type.MemberType;
import jakarta.validation.constraints.NotNull;
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
  @NotNull
  private List<MemberType> memberTypes;

  /** Leadership Builder. */
  @Builder(builderMethodName = "leadershipMemberBuilder")
  @SuppressWarnings("PMD.ExcessiveParameterList")
  public LeadershipMember(
      final Long id,
      final String fullName,
      final String position,
      final String email,
      final String slackDisplayName,
      final Country country,
      final String city,
      final String companyName,
      final List<MemberType> memberTypes,
      final List<Image> images,
      final List<SocialNetwork> network) {
    super(
        id,
        fullName,
        position,
        email,
        slackDisplayName,
        country,
        city,
        companyName,
        memberTypes,
        images,
        network,
        null,
        null);

    this.memberTypes = memberTypes;
  }
}
