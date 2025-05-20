package com.wcc.platform.domain.platform;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** Represents the mentor members of the community. */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor
public class Mentor extends Member {

  private @NotBlank ProfileStatus profileStatus;
  private @NotBlank Skills skills;
  private List<String> spokenLanguages;
  private @NotBlank String bio;

  /** Mentor Builder. */
  @Builder(builderMethodName = "mentorBuilder")
  @SuppressWarnings("PMD.ExcessiveParameterList")
  public Mentor(
      @NotBlank String fullName,
      @NotBlank String position,
      @NotBlank @Email String email,
      String slackDisplayName,
      @NotBlank Country country,
      @NotBlank String city,
      String companyName,
      @NotEmpty List<Image> images,
      List<SocialNetwork> network,
      @NotBlank ProfileStatus profileStatus,
      List<String> spokenLanguages,
      @NotBlank String bio,
      @NotBlank Skills skills) {
    super(
        fullName,
        position,
        email,
        slackDisplayName,
        country,
        city,
        companyName,
        Collections.singletonList(MemberType.MENTOR),
        images,
        network);

    this.profileStatus = profileStatus;
    this.skills = skills;
    this.spokenLanguages = spokenLanguages;
    this.bio = bio;
  }
}
