package com.wcc.platform.factories;

import static com.wcc.platform.factories.SetupFactories.createMemberTest;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.attributes.ImageType;
import com.wcc.platform.domain.cms.attributes.Languages;
import com.wcc.platform.domain.cms.attributes.MentorshipFocusArea;
import com.wcc.platform.domain.cms.attributes.TechnicalArea;
import com.wcc.platform.domain.cms.pages.mentorship.LongTermMentorship;
import com.wcc.platform.domain.cms.pages.mentorship.MenteeSection;
import com.wcc.platform.domain.cms.pages.mentorship.MentorMonthAvailability;
import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.domain.platform.SocialNetworkType;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.domain.platform.mentorship.Mentor.MentorBuilder;
import com.wcc.platform.domain.platform.mentorship.MentorDto;
import com.wcc.platform.domain.platform.mentorship.Skills;
import com.wcc.platform.domain.platform.type.MemberType;
import com.wcc.platform.domain.platform.type.ResourceType;
import com.wcc.platform.domain.resource.MemberProfilePicture;
import com.wcc.platform.domain.resource.Resource;
import java.time.Month;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/** Mentorship test factories. */
public class SetupMentorFactories {

  /** Mentor Builder. */
  public static Mentor createMentorTest() {
    final Member member = createMemberTest(MemberType.MENTOR);
    return createMentorTest(1L, member.getFullName(), member.getEmail());
  }

  /** Test factory for Mentor. */
  public static Mentor createMentorTest(
      final Long mentorId, final String name, final String email) {
    final Member member = createMemberTest(MemberType.MENTOR);

    MentorBuilder mentorBuilder =
        Mentor.mentorBuilder()
            .fullName(name)
            .position(member.getPosition())
            .email(email)
            .slackDisplayName(member.getSlackDisplayName())
            .country(member.getCountry())
            .images(member.getImages())
            .profileStatus(ProfileStatus.PENDING)
            .bio("Mentor bio")
            .spokenLanguages(List.of("english", "spanish", "german"))
            .skills(
                new Skills(
                    2,
                    List.of(TechnicalArea.BACKEND, TechnicalArea.FRONTEND),
                    List.of(Languages.JAVASCRIPT),
                    List.of(MentorshipFocusArea.GROW_BEGINNER_TO_MID)))
            .menteeSection(
                new MenteeSection(
                    "ideal mentee description",
                    "additional",
                    new LongTermMentorship(1, 4),
                    List.of(new MentorMonthAvailability(Month.APRIL, 2))));
    if (mentorId != null) {
      mentorBuilder.id(mentorId);
    }

    return mentorBuilder.build();
  }

  /** Mentor Builder. */
  public static Mentor createMentorTest(final String mentorName) {
    return createMentorTest(1L, mentorName, "member@wcc.com");
  }

  /** Factory test to create MemberDto object. */
  public static MentorDto createMentorDtoTest(final Long mentorId, final MemberType type) {
    return MentorDto.mentorDtoBuilder()
        .id(mentorId)
        .fullName("fullName " + type.name())
        .position("position " + type.name())
        .email("email@" + type.name().toLowerCase())
        .slackDisplayName("slackDisplayName")
        .country(new Country("ES", "Spain"))
        .city("City")
        .companyName("Company name")
        .images(List.of(new Image("image.png", "alt image", ImageType.MOBILE)))
        .network(List.of(new SocialNetwork(SocialNetworkType.GITHUB, "collaborator_link_updated")))
        .profileStatus(ProfileStatus.ACTIVE)
        .bio("Mentor bio")
        .spokenLanguages(List.of("English"))
        .skills(
            new Skills(
                2,
                List.of(TechnicalArea.BACKEND, TechnicalArea.FRONTEND),
                List.of(Languages.JAVASCRIPT),
                List.of(MentorshipFocusArea.GROW_BEGINNER_TO_MID)))
        .menteeSection(
            new MenteeSection(
                "ideal mentee description",
                "additional",
                new LongTermMentorship(1, 4),
                List.of(new MentorMonthAvailability(Month.MARCH, 2))))
        .build();
  }

  /** Factory test to create MemberDto object with long-term and ad-hoc availability params. */
  public static MentorDto createMentorDtoTest(
      final Long mentorId,
      final MemberType type,
      final LongTermMentorship longTerm,
      final List<MentorMonthAvailability> adHocAvailability) {
    return MentorDto.mentorDtoBuilder()
        .id(mentorId)
        .fullName("fullName " + type.name())
        .position("position " + type.name())
        .email("email@" + type.name().toLowerCase())
        .slackDisplayName("slackDisplayName")
        .country(new Country("ES", "Spain"))
        .city("City")
        .companyName("Company name")
        .images(List.of(new Image("image.png", "alt image", ImageType.MOBILE)))
        .network(List.of(new SocialNetwork(SocialNetworkType.GITHUB, "collaborator_link_updated")))
        .profileStatus(ProfileStatus.ACTIVE)
        .bio("Mentor bio")
        .spokenLanguages(List.of("English"))
        .skills(
            new Skills(
                2,
                List.of(TechnicalArea.BACKEND, TechnicalArea.FRONTEND),
                List.of(Languages.JAVASCRIPT),
                List.of(MentorshipFocusArea.GROW_BEGINNER_TO_MID)))
        .menteeSection(
            new MenteeSection(
                "ideal mentee description", "additional", longTerm, adHocAvailability))
        .build();
  }

  /** Test factory for updated Mentor. */
  public static Mentor createUpdatedMentorTest(final Mentor mentor, final MentorDto mentorDto) {

    return Mentor.mentorBuilder()
        .id(mentor.getId())
        .fullName(mentorDto.getFullName())
        .position(mentorDto.getPosition())
        .email(mentorDto.getEmail())
        .slackDisplayName(mentorDto.getSlackDisplayName())
        .country(mentorDto.getCountry())
        .images(mentorDto.getImages())
        .profileStatus(ProfileStatus.ACTIVE)
        .bio("Mentor bio UPDATED")
        .spokenLanguages(List.of("English", "German"))
        .skills(
            new Skills(
                5,
                List.of(TechnicalArea.BACKEND),
                List.of(Languages.JAVASCRIPT, Languages.C_LANGUAGE),
                List.of(MentorshipFocusArea.CHANGE_SPECIALISATION)))
        .menteeSection(
            new MenteeSection(
                "ideal mentee description UPDATED",
                "additional UPDATED",
                null,
                List.of(new MentorMonthAvailability(Month.JUNE, 2))))
        .build();
  }

  /** Test factory for updated Mentor with long-term and ad-hoc availability params. */
  public static Mentor createUpdatedMentorTest(
      final Mentor mentor,
      final MentorDto mentorDto,
      final LongTermMentorship longTerm,
      final List<MentorMonthAvailability> adHocAvailability) {

    return Mentor.mentorBuilder()
        .id(mentor.getId())
        .fullName(mentorDto.getFullName())
        .position(mentorDto.getPosition())
        .email(mentorDto.getEmail())
        .slackDisplayName(mentorDto.getSlackDisplayName())
        .country(mentorDto.getCountry())
        .images(mentorDto.getImages())
        .profileStatus(ProfileStatus.ACTIVE)
        .bio("Mentor bio UPDATED")
        .spokenLanguages(List.of("English", "German"))
        .skills(
            new Skills(
                5,
                List.of(TechnicalArea.BACKEND),
                List.of(Languages.JAVASCRIPT, Languages.C_LANGUAGE),
                List.of(MentorshipFocusArea.CHANGE_SPECIALISATION)))
        .menteeSection(
            new MenteeSection(
                "ideal mentee description UPDATED",
                "additional UPDATED",
                longTerm,
                adHocAvailability))
        .build();
  }

  /** Creates a test MemberProfilePicture with associated Resource. */
  public static MemberProfilePicture createMemberProfilePictureTest(final Long memberId) {
    return MemberProfilePicture.builder()
        .memberId(memberId)
        .resourceId(UUID.randomUUID())
        .resource(createResourceTest())
        .build();
  }

  /** Creates a test Resource for profile pictures. */
  public static Resource createResourceTest() {
    return Resource.builder()
        .id(UUID.randomUUID())
        .name("Profile Picture")
        .description("Test profile picture")
        .fileName("test-profile.jpg")
        .contentType("image/jpeg")
        .size(1024L)
        .driveFileId("test-drive-file-id")
        .driveFileLink("https://drive.google.com/file/d/test-drive-file-id/view")
        .resourceType(ResourceType.PROFILE_PICTURE)
        .createdAt(OffsetDateTime.now())
        .updatedAt(OffsetDateTime.now())
        .build();
  }
}
