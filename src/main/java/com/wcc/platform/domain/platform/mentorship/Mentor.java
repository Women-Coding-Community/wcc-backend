package com.wcc.platform.domain.platform.mentorship;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.attributes.PronounCategory;
import com.wcc.platform.domain.cms.pages.mentorship.FeedbackSection;
import com.wcc.platform.domain.cms.pages.mentorship.MenteeSection;
import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.platform.mentorship.MentorDto.MentorDtoBuilder;
import com.wcc.platform.domain.platform.type.MemberType;
import com.wcc.platform.domain.resource.MentorResource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

/** Represents the mentor members of the community. */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor
@Validated
@SuperBuilder(builderMethodName = "mentorBuilder", toBuilder = true)
@SuppressWarnings("PMD.ImmutableField")
public class Mentor extends Member {

  private @NotNull ProfileStatus profileStatus;
  private @NotNull Skills skills;
  private List<String> spokenLanguages;
  @NotBlank private String bio;
  @NotNull private MenteeSection menteeSection;
  @NotEmpty private List<MemberType> memberTypes;
  private FeedbackSection feedbackSection;
  private MentorResource resources;
  private String calendlyLink;
  private Boolean acceptMale;
  private Boolean acceptPromotion;
  private String meetingLink;

  /** Mentor Constructor. */
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
      final String pronouns,
      final PronounCategory pronounCategory,
      final ProfileStatus profileStatus,
      final List<String> spokenLanguages,
      final String bio,
      final Skills skills,
      final MenteeSection menteeSection,
      final FeedbackSection feedbackSection,
      final MentorResource resources,
      final Boolean isWomen,
      final String calendlyLink,
      final Boolean acceptMale,
      final Boolean acceptPromotion,
      final List<MemberType> memberTypes,
      final String meetingLink) {
    super(
        id,
        fullName,
        position,
        email,
        slackDisplayName,
        country,
        city,
        companyName,
        memberTypes,
        images,
        network,
        pronouns,
        pronounCategory,
        isWomen);

    this.profileStatus = profileStatus;
    this.skills = skills;
    this.spokenLanguages = normalizeLanguages(spokenLanguages);
    this.bio = bio;
    this.menteeSection = menteeSection;
    this.feedbackSection = feedbackSection;
    this.resources = resources;
    this.calendlyLink = calendlyLink;
    this.acceptMale = acceptMale;
    this.acceptPromotion = acceptPromotion;
    this.memberTypes = memberTypes;
    this.meetingLink = meetingLink;
  }

  @Override
  public void setMemberTypes(final List<MemberType> memberTypes) {
    super.setMemberTypes(memberTypes);
    this.memberTypes = memberTypes;
  }

  /** Checks for empty or null and returns a capitalized list of string. */
  private static List<String> normalizeLanguages(final List<String> languages) {
    if (CollectionUtils.isEmpty(languages)) {
      return List.of();
    }

    return languages.stream().filter(StringUtils::isNotBlank).map(StringUtils::capitalize).toList();
  }

  /**
   * Converts this Mentor entity and an optional active MentorshipCycle into a MentorDto
   * representation.
   *
   * @param mentorshipCycle an optional MentorshipCycle representing the active mentorship cycle
   * @return a MentorDto containing the mentor's details and availability information
   */
  public MentorDto toDto(final MentorshipCycle mentorshipCycle) {
    return buildFromMentor(this).build();
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
        .pronouns(mentor.getPronouns())
        .pronounCategory(mentor.getPronounCategory())
        .spokenLanguages(mentor.getSpokenLanguages())
        .bio(mentor.getBio())
        .skills(mentor.getSkills())
        .menteeSection(mentor.getMenteeSection().toDto())
        .feedbackSection(mentor.getFeedbackSection())
        .resources(mentor.getResources())
        .isWomen(mentor.getIsWomen())
        .calendlyLink(mentor.getCalendlyLink())
        .acceptMale(mentor.getAcceptMale())
        .acceptPromotion(mentor.getAcceptPromotion())
        .meetingLink(mentor.getMeetingLink());
  }

  /** Mentor Builder implementation to ensure proper inheritance. */
  @SuppressWarnings("unchecked")
  public abstract static class MentorBuilder<C extends Mentor, B extends MentorBuilder<C, B>>
      extends MemberBuilder<C, B> {

    public B profileStatus(final ProfileStatus profileStatus) {
      this.profileStatus = profileStatus;
      return (B) this;
    }

    public B skills(final Skills skills) {
      this.skills = skills;
      return (B) this;
    }

    public B spokenLanguages(final List<String> spokenLanguages) {
      this.spokenLanguages = normalizeLanguages(spokenLanguages);
      return (B) this;
    }

    public B bio(final String bio) {
      this.bio = bio;
      return (B) this;
    }

    public B menteeSection(final MenteeSection menteeSection) {
      this.menteeSection = menteeSection;
      return (B) this;
    }

    public B feedbackSection(final FeedbackSection feedbackSection) {
      this.feedbackSection = feedbackSection;
      return (B) this;
    }

    public B resources(final MentorResource resources) {
      this.resources = resources;
      return (B) this;
    }

    public B calendlyLink(final String calendlyLink) {
      this.calendlyLink = calendlyLink;
      return (B) this;
    }

    public B acceptMale(final Boolean acceptMale) {
      this.acceptMale = acceptMale;
      return (B) this;
    }

    public B acceptPromotion(final Boolean acceptPromotion) {
      this.acceptPromotion = acceptPromotion;
      return (B) this;
    }

    public B meetingLink(final String meetingLink) {
      this.meetingLink = meetingLink;
      return (B) this;
    }

    @Override
    public B memberTypes(final List<MemberType> memberTypes) {
      this.memberTypes = memberTypes;
      return (B) this;
    }
  }
}
