package com.wcc.platform.domain.platform.mentorship;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.pages.mentorship.FeedbackSection;
import com.wcc.platform.domain.cms.pages.mentorship.MenteeSection;
import com.wcc.platform.domain.exceptions.InvalidMentorException;
import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.domain.platform.member.MemberDto;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.resource.MentorResource;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.CollectionUtils;

/** Represents the mentor members of the community. */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor
@SuppressWarnings("PMD.ImmutableField")
public class MentorDto extends MemberDto {

  private ProfileStatus profileStatus;
  private Skills skills;
  private List<String> spokenLanguages;
  private String bio;
  private MenteeSection menteeSection;
  private FeedbackSection feedbackSection;
  private MentorResource resources;

  /** Mentor Builder. */
  @SuppressWarnings("PMD.ExcessiveParameterList")
  @Builder(builderMethodName = "mentorDtoBuilder")
  public MentorDto(
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
      final ProfileStatus profileStatus,
      @NotEmpty final List<String> spokenLanguages,
      @NotBlank final String bio,
      @NotNull final Skills skills,
      @NotNull final MenteeSection menteeSection,
      final FeedbackSection feedbackSection,
      final MentorResource resources) {
    super(
        id,
        fullName,
        position,
        email,
        slackDisplayName,
        country,
        city,
        companyName,
        null, // TODO to be fixe this will cleanup member types
        images,
        network);
    this.skills = skills;
    this.spokenLanguages = spokenLanguages;
    this.bio = bio;
    this.menteeSection = menteeSection;
    this.feedbackSection = feedbackSection;
    this.resources = resources;
    this.profileStatus = profileStatus;
  }

  /**
   * Merges this DTO with an existing Mentor entity. Non-null/non-blank DTO values override existing
   * values; otherwise existing values are retained.
   *
   * @param mentor the existing mentor to merge with
   * @return merged Mentor with updated fields
   */
  public Mentor merge(final Mentor mentor) {
    if (mentor == null) {
      throw new InvalidMentorException("Cannot merge with null mentor");
    }

    return Mentor.mentorBuilder()
        .id(mentor.getId())
        .fullName(mergeString(this.getFullName(), mentor.getFullName()))
        .position(mergeString(this.getPosition(), mentor.getPosition()))
        .email(mergeString(this.getEmail(), mentor.getEmail()))
        .slackDisplayName(mergeString(this.getSlackDisplayName(), mentor.getSlackDisplayName()))
        .city(mergeString(this.getCity(), mentor.getCity()))
        .companyName(mergeString(this.getCompanyName(), mentor.getCompanyName()))
        .country(mergeNullable(this.getCountry(), mentor.getCountry()))
        .profileStatus(mergeNullable(this.getProfileStatus(), mentor.getProfileStatus()))
        .bio(mergeString(this.getBio(), mentor.getBio()))
        .skills(mergeNullable(this.getSkills(), mentor.getSkills()))
        .menteeSection(mergeNullable(this.getMenteeSection(), mentor.getMenteeSection()))
        .feedbackSection(mergeNullable(this.getFeedbackSection(), mentor.getFeedbackSection()))
        .resources(mergeNullable(this.getResources(), mentor.getResources()))
        .network(mergeCollection(this.getNetwork(), mentor.getNetwork()))
        .spokenLanguages(mergeCollection(this.getSpokenLanguages(), mentor.getSpokenLanguages()))
        .images(mergeCollection(this.getImages(), mentor.getImages()))
        .build();
  }

  private String mergeString(final String candidate, final String existing) {
    return StringUtils.isNotBlank(candidate) ? candidate : existing;
  }

  private <T> T mergeNullable(final T candidate, final T existing) {
    return candidate != null ? candidate : existing;
  }

  private <T> List<T> mergeCollection(final List<T> candidate, final List<T> existing) {
    if (!CollectionUtils.isEmpty(candidate)) {
      return candidate;
    }
    return existing != null ? existing : List.of();
  }
}
