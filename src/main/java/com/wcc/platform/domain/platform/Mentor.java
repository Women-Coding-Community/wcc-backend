package com.wcc.platform.domain.platform;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.pages.mentorship.FeedbackSection;
import com.wcc.platform.domain.cms.pages.mentorship.MenteeSection;
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

  @SuppressWarnings("PMD.ImmutableField")
  private @NotBlank ProfileStatus profileStatus;

  @SuppressWarnings("PMD.ImmutableField")
  private @NotBlank Skills skills;

  @SuppressWarnings("PMD.ImmutableField")
  private List<String> spokenLanguages;

  @SuppressWarnings("PMD.ImmutableField")
  private @NotBlank String bio;

  @SuppressWarnings("PMD.ImmutableField")
  private @NotBlank MenteeSection menteeSection;

  @SuppressWarnings("PMD.ImmutableField")
  private FeedbackSection feedbackSection;

  @SuppressWarnings("PMD.ImmutableField")
  private List<ResourceContent> resources;

  /** Mentor Builder. */
  @Builder(builderMethodName = "mentorBuilder")
  @SuppressWarnings("PMD.ExcessiveParameterList")
  public Mentor(
      @NotBlank final String fullName,
      @NotBlank final String position,
      @NotBlank @Email final String email,
      final String slackDisplayName,
      @NotBlank final Country country,
      @NotBlank final String city,
      final String companyName,
      @NotEmpty final List<Image> images,
      final List<SocialNetwork> network,
      @NotBlank final ProfileStatus profileStatus,
      final List<String> spokenLanguages,
      @NotBlank final String bio,
      @NotBlank final Skills skills,
      @NotBlank final MenteeSection menteeSection,
      final FeedbackSection feedbackSection,
      final List<ResourceContent> resources) {
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
    this.menteeSection = menteeSection;
    this.feedbackSection = feedbackSection;
    this.resources = resources;
  }
}
