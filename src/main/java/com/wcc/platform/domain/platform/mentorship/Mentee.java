package com.wcc.platform.domain.platform.mentorship;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.attributes.PronounCategory;
import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.platform.type.MemberType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Getter
@NoArgsConstructor
@SuppressWarnings({"PMD.ExcessiveParameterList", "PMD.ImmutableField"})
public class Mentee extends Member {

  private @NotNull ProfileStatus profileStatus;
  private @NotNull Skills skills;
  private @NotBlank String bio;
  private List<String> spokenLanguages;

  @Builder(builderMethodName = "menteeBuilder")
  public Mentee(
      final Long id,
      final String fullName,
      final String position,
      final String email,
      final String slackDisplayName,
      final Country country,
      final String city,
      final String companyName,
      final List<Image> images,
      final List<SocialNetwork> network,
      final String pronouns,
      final PronounCategory pronounCategory,
      final Boolean isWomenNonBinary,
      final ProfileStatus profileStatus,
      final List<String> spokenLanguages,
      final String bio,
      final Skills skills) {
    super(
        id,
        fullName,
        position,
        email,
        slackDisplayName,
        country,
        city,
        companyName,
        Collections.singletonList(MemberType.MENTEE),
        images,
        network,
        pronouns,
        pronounCategory,
        isWomenNonBinary);

    this.profileStatus = profileStatus;
    this.skills = skills;
    this.spokenLanguages = spokenLanguages.stream().map(StringUtils::capitalize).toList();
    this.bio = bio;
  }
}
