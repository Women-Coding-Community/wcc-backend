package com.wcc.platform.domain.platform.mentorship;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.platform.type.MemberType;
import jakarta.validation.constraints.Email;
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
      @NotBlank final String fullName,
      @NotBlank final String position,
      @NotBlank @Email final String email,
      @NotBlank final String slackDisplayName,
      @NotNull final Country country,
      @NotBlank final String city,
      final String companyName,
      final List<Image> images,
      final List<SocialNetwork> network,
      @NotNull final ProfileStatus profileStatus,
      final List<String> spokenLanguages, // TODO
      @NotBlank final String bio,
      @NotNull final Skills skills) {
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
        network);

    this.profileStatus = profileStatus;
    this.skills = skills;
    this.spokenLanguages = spokenLanguages.stream().map(StringUtils::capitalize).toList();
    this.bio = bio;
  }
}
