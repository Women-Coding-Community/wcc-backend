package com.wcc.platform.domain.platform.mentorship;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.pages.mentorship.FeedbackSection;
import com.wcc.platform.domain.cms.pages.mentorship.MenteeSection;
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
  private MentorAvailability availability;
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
      final MentorResource resources,
      final MentorAvailability availability) {
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
    this.availability = availability;
    this.skills = skills;
    this.spokenLanguages = spokenLanguages;
    this.bio = bio;
    this.menteeSection = menteeSection;
    this.feedbackSection = feedbackSection;
    this.resources = resources;
    this.profileStatus = profileStatus;
  }

  /**
   * Merges the current Mentor instance with the attributes of the provided Mentor instance.
   * Combines properties from both instances into a new Mentor object, giving precedence
   * to non-null values in the provided Mentor instance while retaining existing values
   * where the provided values are null or empty.
   *
   * @param mentor the Mentor object containing updated attributes to merge with the current instance
   * @return a new Mentor object created by merging attributes from the current instance and the provided instance
   */
  public Mentor merge(final Mentor mentor) {
    var member = super.merge(mentor);

    final Mentor.MentorBuilder builder =
        Mentor.mentorBuilder()
            .id(member.getId())
            .fullName(mergeString(this.getFullName(), member.getFullName()))
            .position(mergeString(this.getPosition(), member.getPosition()))
            .email(mergeString(this.getEmail(), member.getEmail()))
            .slackDisplayName(mergeString(this.getSlackDisplayName(), member.getSlackDisplayName()))
            .country(mergeNullable(this.getCountry(), member.getCountry()))
            .profileStatus(mergeNullable(this.profileStatus, mentor.getProfileStatus()))
            .bio(mergeString(this.bio, mentor.getBio()))
            .skills(mergeNullable(this.skills, mentor.getSkills()))
            .menteeSection(mergeNullable(this.menteeSection, mentor.getMenteeSection()));

    mergeOptionalString(this.getCity(), member.getCity(), builder::city);

    mergeOptionalString(this.getCompanyName(), member.getCompanyName(), builder::companyName);

    builder.network(mergeCollection(this.getNetwork(), member.getNetwork()));
    builder.spokenLanguages(
        mergeCollection(this.getSpokenLanguages(), mentor.getSpokenLanguages()));
    builder.images(mergeCollection(this.getImages(), member.getImages()));

    mergeOptional(this.feedbackSection, mentor.getFeedbackSection(), builder::feedbackSection);

    mergeOptional(this.resources, mentor.getResources(), builder::resources);

    return builder.build();
  }

  private String mergeString(final String candidate, final String existing) {
    return StringUtils.isNotBlank(candidate) ? candidate : existing;
  }

  private <T> T mergeNullable(final T candidate, final T existing) {
    return candidate != null ? candidate : existing;
  }

  private <T> List<T> mergeCollection(final List<T> candidate, final List<T> existing) {
    return CollectionUtils.isEmpty(candidate) ? existing : candidate;
  }

  private void mergeOptionalString(
      final String candidate,
      final String existing,
      final java.util.function.Consumer<String> setter) {

    if (StringUtils.isNotBlank(candidate) || StringUtils.isNotBlank(existing)) {
      setter.accept(mergeString(candidate, existing));
    }
  }

  private <T> void mergeOptional(
      final T candidate, final T existing, final java.util.function.Consumer<T> setter) {

    if (candidate != null || existing != null) {
      setter.accept(mergeNullable(candidate, existing));
    }
  }
}
