package com.wcc.platform.domain.platform.mentorship;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.pages.mentorship.FeedbackSection;
import com.wcc.platform.domain.cms.pages.mentorship.MenteeSection;
import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.platform.type.MemberType;
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

@Getter
@ToString
@NoArgsConstructor
@SuppressWarnings("PMD.ImmutableField")
public class CreateMentorRequest {

  @NotBlank private String fullName;
  @NotBlank private String position;
  @NotBlank @Email private String email;
  @NotBlank private String slackDisplayName;
  @NotNull private Country country;
  @NotBlank private String city;
  private String companyName;
  @NotEmpty private List<MemberType> memberTypes;
  private List<Image> images;
  private List<SocialNetwork> network;

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
      final List<MemberType> memberTypes,
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
    this.memberTypes = memberTypes;
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
        .fullName(fullName)
        .position(position)
        .email(email)
        .slackDisplayName(slackDisplayName)
        .country(country)
        .city(city)
        .companyName(companyName)
        .images(images != null ? images : List.of())
        .network(network != null ? network : List.of())
        .profileStatus(ProfileStatus.PENDING)
        .spokenLanguages(spokenLanguages != null ? spokenLanguages : List.of())
        .bio(bio)
        .skills(skills)
        .menteeSection(menteeSection)
        .feedbackSection(feedbackSection)
        .resources(resources)
        .build();
  }
}
