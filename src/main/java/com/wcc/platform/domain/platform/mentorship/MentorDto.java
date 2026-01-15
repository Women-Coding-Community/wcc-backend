package com.wcc.platform.domain.platform.mentorship;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.pages.mentorship.FeedbackSection;
import com.wcc.platform.domain.cms.pages.mentorship.MenteeSection;
import com.wcc.platform.domain.exceptions.InvalidMentorException;
import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.domain.platform.member.Member;
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
      @NotBlank final Country country,
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
   * Merge this DTO with an existing Mentor entity.
   *
   * @param member the existing mentor to merge with
   * @return Updated mentor
   * @throws InvalidMentorException if member is null
   * @throws IllegalArgumentException if member is not a Mentor instance
   */
  @Override
  public Member merge(final Member member) {
    if (member == null) {
      throw new InvalidMentorException("Cannot merge with null mentor");
    }
    if (!(member instanceof Mentor existingMentor)) {
      throw new InvalidMentorException(
          "Expected Mentor instance but got: " + member.getClass().getSimpleName());
    }

    final Mentor.MentorBuilder builder =
        Mentor.mentorBuilder()
            .id(existingMentor.getId())
            .fullName(mergeString(this.getFullName(), existingMentor.getFullName()))
            .position(mergeString(this.getPosition(), existingMentor.getPosition()))
            .email(mergeString(this.getEmail(), existingMentor.getEmail()))
            .slackDisplayName(
                mergeString(this.getSlackDisplayName(), existingMentor.getSlackDisplayName()))
            .country(mergeNullable(this.getCountry(), existingMentor.getCountry()))
            .profileStatus(mergeNullable(this.profileStatus, existingMentor.getProfileStatus()))
            .bio(mergeString(this.bio, existingMentor.getBio()))
            .skills(mergeNullable(this.skills, existingMentor.getSkills()))
            .menteeSection(mergeNullable(this.menteeSection, existingMentor.getMenteeSection()));

    mergeOptionalString(this.getCity(), existingMentor.getCity(), builder::city);

    mergeOptionalString(
        this.getCompanyName(), existingMentor.getCompanyName(), builder::companyName);

    builder.network(mergeCollection(this.getNetwork(), existingMentor.getNetwork()));
    builder.spokenLanguages(
        mergeCollection(this.getSpokenLanguages(), existingMentor.getSpokenLanguages()));
    builder.images(mergeCollection(this.getImages(), existingMentor.getImages()));

    mergeOptional(
        this.feedbackSection, existingMentor.getFeedbackSection(), builder::feedbackSection);

    mergeOptional(this.resources, existingMentor.getResources(), builder::resources);

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
