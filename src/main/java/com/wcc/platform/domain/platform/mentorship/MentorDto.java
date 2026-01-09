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
    final Member mergedMember = super.merge(member);
    final Mentor mentor = (Mentor) mergedMember;

    // Build updated Mentor with Mentor-specific fields
    return Mentor.mentorBuilder()
        .id(mentor.getId())
        .fullName(mentor.getFullName())
        .position(mentor.getPosition())
        .email(mentor.getEmail())
        .slackDisplayName(mentor.getSlackDisplayName())
        .country(mentor.getCountry())
        .city(mentor.getCity())
        .companyName(mentor.getCompanyName())
        .images(mentor.getImages())
        .network(mentor.getNetwork())
        .profileStatus(
            this.profileStatus != null ? this.profileStatus : ((Mentor) member).getProfileStatus())
        .spokenLanguages(
            this.spokenLanguages != null
                ? this.spokenLanguages
                : ((Mentor) member).getSpokenLanguages())
        .bio(this.bio != null ? this.bio : ((Mentor) member).getBio())
        .skills(this.skills != null ? this.skills : ((Mentor) member).getSkills())
        .menteeSection(
            this.menteeSection != null ? this.menteeSection : ((Mentor) member).getMenteeSection())
        .feedbackSection(
            this.feedbackSection != null
                ? this.feedbackSection
                : ((Mentor) member).getFeedbackSection())
        .resources(this.resources != null ? this.resources : ((Mentor) member).getResources())
        .build();
  }
}
