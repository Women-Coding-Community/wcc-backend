package com.wcc.platform.domain.platform.mentorship;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.pages.mentorship.FeedbackSection;
import com.wcc.platform.domain.cms.pages.mentorship.MenteeSection;
import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.resource.MentorResource;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** Request body for the Mentor members of the community. */
@Getter
@ToString
@NoArgsConstructor
@SuppressWarnings("PMD.ImmutableField")
public class CreateMentorRequest {

  // Member-like fields
  @NotBlank private String fullName;
  @NotBlank private String position;
  @NotBlank @Email private String email;
  @NotBlank private String slackDisplayName;
  @NotNull private Country country;
  @NotBlank private String city;

  private String companyName;
  private List<Image> images;
  private List<SocialNetwork> network;

  // Mentor-specific fields
  @NotEmpty private List<String> spokenLanguages;
  @NotBlank private String bio;
  @NotNull private Skills skills;
  @NotNull private MenteeSection menteeSection;

  private FeedbackSection feedbackSection;
  private MentorResource resources;

  @Builder(builderMethodName = "createMentorRequestBuilder")
  @SuppressWarnings("PMD.ExcessiveParameterList")
  public CreateMentorRequest(
      final String fullName,
      final String position,
      final String email,
      final String slackDisplayName,
      final Country country,
      final String city,
      final String companyName,
      final List<Image> images,
      final List<SocialNetwork> network,
      final List<String> spokenLanguages,
      final String bio,
      final Skills skills,
      final MenteeSection menteeSection,
      final FeedbackSection feedbackSection,
      final MentorResource resources) {
    this.fullName = fullName;
    this.position = position;
    this.email = email;
    this.slackDisplayName = slackDisplayName;
    this.country = country;
    this.city = city;
    this.companyName = companyName;
    this.images = images;
    this.network = network;
    this.spokenLanguages = spokenLanguages;
    this.bio = bio;
    this.skills = skills;
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
