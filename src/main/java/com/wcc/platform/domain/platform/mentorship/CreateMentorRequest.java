package com.wcc.platform.domain.platform.mentorship;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.pages.mentorship.FeedbackSection;
import com.wcc.platform.domain.cms.pages.mentorship.MenteeSection;
import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.domain.platform.member.MemberDto;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.resource.MentorResource;
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

/** Request body for the Mentor members of the community. */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor
@SuppressWarnings("PMD.ImmutableField")
public class CreateMentorRequest extends MemberDto {

  private Skills skills;
  private List<String> spokenLanguages;
  private String bio;
  private MenteeSection menteeSection;
  private FeedbackSection feedbackSection;
  private MentorResource resources;

  @Builder(builderMethodName = "createMentorRequestBuilder")
  @SuppressWarnings("PMD.ExcessiveParameterList")
  public CreateMentorRequest(
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
    this.skills = skills;
    this.spokenLanguages = spokenLanguages;
    this.bio = bio;
    this.menteeSection = menteeSection;
    this.feedbackSection = feedbackSection;
    this.resources = resources;
  }

  public Mentor toMentor() {
    return Mentor.mentorBuilder()
        .id(null)
        .fullName(getFullName())
        .position(getPosition())
        .email(getEmail())
        .slackDisplayName(getSlackDisplayName())
        .country(getCountry())
        .city(getCity())
        .companyName(getCompanyName())
        .images(getImages() != null ? getImages() : List.of())
        .network(getNetwork() != null ? getNetwork() : List.of())
        .profileStatus(ProfileStatus.PENDING)
        .spokenLanguages(getSpokenLanguages() != null ? getSpokenLanguages() : List.of())
        .bio(getBio())
        .skills(getSkills())
        .menteeSection(getMenteeSection())
        .feedbackSection(getFeedbackSection())
        .resources(getResources())
        .build();
  }
}
