package com.wcc.platform.domain.platform.mentorship;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.pages.mentorship.FeedbackSection;
import com.wcc.platform.domain.cms.pages.mentorship.MenteeSection;
import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.platform.mentorship.MentorDto.MentorDtoBuilder;
import com.wcc.platform.domain.platform.type.MemberType;
import com.wcc.platform.domain.resource.MentorResource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/** Represents the mentor members of the community. */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor
@SuppressWarnings("PMD.ImmutableField")
public class Mentor extends Member {

  private @NotNull ProfileStatus profileStatus;
  private @NotNull Skills skills;
  private List<String> spokenLanguages;
  @NotBlank private String bio;
  @NotNull private MenteeSection menteeSection;
  private FeedbackSection feedbackSection;
  private MentorResource resources;

  /** Mentor Builder. */
  @Builder(builderMethodName = "mentorBuilder")
  @SuppressWarnings("PMD.ExcessiveParameterList")
  public Mentor(
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
        Collections.singletonList(MemberType.MENTOR),
        images,
        network);

    this.profileStatus = profileStatus;
    this.skills = skills;
    this.spokenLanguages = spokenLanguages.stream().map(StringUtils::capitalize).toList();
    this.bio = bio;
    this.menteeSection = menteeSection;
    this.feedbackSection = feedbackSection;
    this.resources = resources;
  }

  /**
   * Converts this Mentor entity and an optional active MentorshipCycle into a MentorDto
   * representation.
   *
   * @param mentorshipCycle an optional MentorshipCycle representing the active mentorship cycle
   * @return a MentorDto containing the mentor's details and availability information
   */
  public MentorDto toDto(final MentorshipCycle mentorshipCycle) {
    final var mentor = this;
    final var mentorBuilder = buildFromMentor(mentor);

    if (mentor.getMenteeSection().mentorshipType().contains(mentorshipCycle.cycle())) {

      final var isAvailable =
          mentor.getMenteeSection().availability().stream()
              .filter(availability -> availability.month() == mentorshipCycle.month())
              .findAny();

      mentorBuilder.availability(
          new MentorAvailability(mentorshipCycle.cycle(), isAvailable.isPresent()));
    }

    return mentorBuilder.build();
  }

  /**
   * Converts this Mentor entity into a MentorDto representation when no mentorship cycle is active.
   *
   * @return a MentorDto object constructed from the provided Mentor entity
   */
  @Override
  public MentorDto toDto() {
    return buildFromMentor(this).build();
  }

  private MentorDtoBuilder buildFromMentor(final Mentor mentor) {
    return MentorDto.mentorDtoBuilder()
        .id(mentor.getId())
        .availability(null)
        .fullName(mentor.getFullName())
        .position(mentor.getPosition())
        .email(mentor.getEmail())
        .slackDisplayName(mentor.getSlackDisplayName())
        .country(mentor.getCountry())
        .city(mentor.getCity())
        .companyName(mentor.getCompanyName())
        .images(mentor.getImages())
        .network(mentor.getNetwork())
        .profileStatus(mentor.getProfileStatus())
        .spokenLanguages(mentor.getSpokenLanguages())
        .bio(mentor.getBio())
        .skills(mentor.getSkills())
        .menteeSection(mentor.getMenteeSection().toDto())
        .feedbackSection(mentor.getFeedbackSection())
        .resources(mentor.getResources());
  }
}
