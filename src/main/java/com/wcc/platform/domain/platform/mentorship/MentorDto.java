package com.wcc.platform.domain.platform.mentorship;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.pages.mentorship.FeedbackSection;
import com.wcc.platform.domain.cms.pages.mentorship.MenteeSection;
import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.domain.platform.member.MemberDto;
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
      final MentorResource resources,
      final MentorAvailability availability) {
    super(id, fullName, position, country, city, companyName, images, network);
    this.availability = availability;
    this.skills = skills;
    this.spokenLanguages = spokenLanguages;
    this.bio = bio;
    this.menteeSection = menteeSection;
    this.feedbackSection = feedbackSection;
    this.resources = resources;
  }
}
