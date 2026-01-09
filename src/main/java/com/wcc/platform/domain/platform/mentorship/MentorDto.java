package com.wcc.platform.domain.platform.mentorship;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.pages.mentorship.FeedbackSection;
import com.wcc.platform.domain.cms.pages.mentorship.MenteeSection;
import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.member.MemberDto;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.resource.MentorResource;
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
      final String fullName,
      final String position,
      final String email,
      final String slackDisplayName,
      final Country country,
      final String city,
      final String companyName,
      final List<Image> images,
      final List<SocialNetwork> network,
      final ProfileStatus profileStatus,
      final List<String> spokenLanguages,
      final String bio,
      final Skills skills,
      final MenteeSection menteeSection,
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
        null,
        images,
        network);
    this.profileStatus = profileStatus;
    this.availability = availability;
    this.skills = skills;
    this.spokenLanguages = spokenLanguages;
    this.bio = bio;
    this.menteeSection = menteeSection;
    this.feedbackSection = feedbackSection;
    this.resources = resources;
  }

  /**
   * Merge this DTO with an existing Mentor entity.
   *
   * @param member the existing mentor to merge with
   * @return Updated mentor
   */
  @Override
  public Member merge(final Member member) {
    // Merge Member fields using parent's merge method
    final Mentor existingMentor = (Mentor) member;

    // Build updated Mentor with Mentor-specific fields
    return Mentor.mentorBuilder()
        .id(existingMentor.getId())
        .fullName(this.getFullName() != null ? this.getFullName() : existingMentor.getFullName())
        .position(this.getPosition() != null ? this.getPosition() : existingMentor.getPosition())
        .email(this.getEmail() != null ? this.getEmail() : existingMentor.getEmail())
        .slackDisplayName(
            this.getSlackDisplayName() != null
                ? this.getSlackDisplayName()
                : existingMentor.getSlackDisplayName())
        .country(this.getCountry() != null ? this.getCountry() : existingMentor.getCountry())
        .city(this.getCity() != null ? this.getCity() : existingMentor.getCity())
        .companyName(
            this.getCompanyName() != null ? this.getCompanyName() : existingMentor.getCompanyName())
        .images(this.getImages() != null ? this.getImages() : existingMentor.getImages())
        .network(this.getNetwork() != null ? this.getNetwork() : existingMentor.getNetwork())
        .profileStatus(
            this.profileStatus != null ? this.profileStatus : existingMentor.getProfileStatus())
        .spokenLanguages(
            this.spokenLanguages != null
                ? this.spokenLanguages
                : existingMentor.getSpokenLanguages())
        .bio(this.bio != null ? this.bio : existingMentor.getBio())
        .skills(this.skills != null ? this.skills : existingMentor.getSkills())
        .menteeSection(
            this.menteeSection != null ? this.menteeSection : existingMentor.getMenteeSection())
        .feedbackSection(
            this.feedbackSection != null
                ? this.feedbackSection
                : existingMentor.getFeedbackSection())
        .resources(this.resources != null ? this.resources : existingMentor.getResources())
        .build();
  }
}
