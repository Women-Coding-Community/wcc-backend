package com.wcc.platform.domain.platform.mentorship;

import static com.wcc.platform.domain.platform.SocialNetworkType.GITHUB;
import static com.wcc.platform.domain.platform.SocialNetworkType.LINKEDIN;
import static com.wcc.platform.factories.SetupMentorFactories.createMentorTest;
import static org.junit.jupiter.api.Assertions.*;

import com.wcc.platform.domain.cms.attributes.CodeLanguage;
import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.attributes.ImageType;
import com.wcc.platform.domain.cms.attributes.MentorshipFocusArea;
import com.wcc.platform.domain.cms.attributes.ProficiencyLevel;
import com.wcc.platform.domain.cms.attributes.TechnicalArea;
import com.wcc.platform.domain.cms.pages.mentorship.LongTermMentorship;
import com.wcc.platform.domain.cms.pages.mentorship.MenteeSection;
import com.wcc.platform.domain.cms.pages.mentorship.MentorMonthAvailability;
import com.wcc.platform.domain.exceptions.InvalidMentorException;
import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MentorDtoTest {

  private Mentor existingMentor;
  private MentorDto mentorDto;

  @BeforeEach
  void setUp() {
    existingMentor = createMentorTest("Original Name");
    existingMentor =
        Mentor.mentorBuilder()
            .id(existingMentor.getId())
            .fullName("Original Name")
            .position("Original Position")
            .email("original@example.com")
            .slackDisplayName("@original")
            .country(new Country("US", "United States"))
            .city("Original City")
            .companyName("Original Company")
            .images(List.of(new Image("profile.jpg", "Profile Image", ImageType.MOBILE)))
            .network(List.of(new SocialNetwork(LINKEDIN, "https://linkedin.com/in/original")))
            .profileStatus(ProfileStatus.PENDING)
            .spokenLanguages(List.of("English", "Spanish"))
            .bio("Original bio")
            .pronouns(null)
            .pronounCategory(null)
            .skills(
                new Skills(
                    10,
                    List.of(
                        new TechnicalAreaProficiency(
                            TechnicalArea.BACKEND, ProficiencyLevel.BEGINNER),
                        new TechnicalAreaProficiency(
                            TechnicalArea.FRONTEND, ProficiencyLevel.BEGINNER)),
                    List.of(
                        new LanguageProficiency(CodeLanguage.JAVA, ProficiencyLevel.BEGINNER),
                        new LanguageProficiency(CodeLanguage.PYTHON, ProficiencyLevel.BEGINNER)),
                    List.of(MentorshipFocusArea.CHANGE_SPECIALISATION)))
            .menteeSection(
                new MenteeSection(
                    "Original ideal mentee",
                    "Original additional info",
                    new LongTermMentorship(1, 4),
                    List.of(new MentorMonthAvailability(Month.JANUARY, 2))))
            .build();
  }

  @Test
  void shouldReturnTrueForEqualMentorDtos() {
    MentorDto mentor1 = MentorDto.mentorDtoBuilder().id(1L).build();
    MentorDto mentor2 = MentorDto.mentorDtoBuilder().id(1L).build();

    assertEquals(mentor1, mentor2);
  }

  @Test
  void shouldReturnFalseForDifferentMentorDtos() {
    MentorDto mentor1 = MentorDto.mentorDtoBuilder().id(1L).build();
    MentorDto mentor2 = MentorDto.mentorDtoBuilder().id(2L).build();

    assertNotEquals(mentor1, mentor2);
  }

  @Test
  void shouldReturnStringOfMentor() {
    MentorDto mentor =
        MentorDto.mentorDtoBuilder()
            .bio("bio info")
            .spokenLanguages(List.of("English", "Spanish"))
            .fullName("Jane Doe")
            .id(1L)
            .profileStatus(ProfileStatus.PENDING)
            .build();
    var expected =
        "MentorDto(profileStatus=PENDING, pronouns=null, pronounCategory=null, "
            + "skills=null, spokenLanguages=[English, Spanish], bio=bio info,"
            + " menteeSection=null, feedbackSection=null, resources=null, "
            + "isWomenNonBinary=null, acceptMale=null, "
            + "acceptPromotion=null)";

    assertEquals(expected, mentor.toString());
  }

  @Test
  void testMergeShouldUpdateAllFields() {
    mentorDto =
        MentorDto.mentorDtoBuilder()
            .id(1L)
            .fullName("Updated Name")
            .position("Updated Position")
            .email("updated@example.com")
            .slackDisplayName("@updated")
            .country(new Country("CA", "Canada"))
            .city("Updated City")
            .companyName("Updated Company")
            .images(List.of(new Image("new.jpg", "New Image", ImageType.DESKTOP)))
            .network(List.of(new SocialNetwork(GITHUB, "https://github.com/new")))
            .spokenLanguages(List.of("French", "German"))
            .bio("Updated bio")
            .skills(
                new Skills(
                    5,
                    List.of(
                        new TechnicalAreaProficiency(
                            TechnicalArea.FULLSTACK, ProficiencyLevel.BEGINNER)),
                    List.of(
                        new LanguageProficiency(CodeLanguage.JAVASCRIPT, ProficiencyLevel.BEGINNER)),
                    List.of(MentorshipFocusArea.CHANGE_SPECIALISATION)))
            .menteeSection(
                new MenteeSection(
                    "New ideal mentee",
                    "New additional info",
                    null,
                    List.of(new MentorMonthAvailability(Month.JUNE, 3))))
            .build();

    Mentor result = mentorDto.merge(existingMentor);

    assertEquals(1L, result.getId());
    assertEquals("Updated Name", result.getFullName());
    assertEquals("Updated Position", result.getPosition());
    assertEquals("updated@example.com", result.getEmail());
    assertEquals("@updated", result.getSlackDisplayName());
    assertEquals("CA", result.getCountry().countryCode());
    assertEquals("Updated City", result.getCity());
    assertEquals("Updated Company", result.getCompanyName());
    assertEquals(ProfileStatus.PENDING, result.getProfileStatus());
    assertEquals("Updated bio", result.getBio());
    assertEquals(List.of("French", "German"), result.getSpokenLanguages());
    assertEquals(5, result.getSkills().yearsExperience());
    assertEquals("New ideal mentee", result.getMenteeSection().idealMentee());
  }

  @Test
  void testMergeShouldRetainExistingValuesWhenDtoFieldsAreNull() {
    mentorDto =
        MentorDto.mentorDtoBuilder().fullName("Updated Name").email("updated@example.com").build();

    Mentor result = mentorDto.merge(existingMentor);

    assertEquals("Updated Name", result.getFullName());
    assertEquals("updated@example.com", result.getEmail());

    assertEquals("Original Position", result.getPosition());
    assertEquals("@original", result.getSlackDisplayName());
    assertEquals("US", result.getCountry().countryCode());
    assertEquals("Original City", result.getCity());
    assertEquals("Original Company", result.getCompanyName());
    assertEquals(ProfileStatus.PENDING, result.getProfileStatus());
    assertEquals("Original bio", result.getBio());
    assertEquals(List.of("English", "Spanish"), result.getSpokenLanguages());
    assertEquals(10, result.getSkills().yearsExperience());
  }

  @Test
  void testMergeShouldRetainsExistingValuesWhenDtoStringsAreBlank() {
    mentorDto =
        MentorDto.mentorDtoBuilder()
            .fullName("")
            .position("   ")
            .email("updated@example.com")
            .bio(null)
            .build();

    Mentor result = mentorDto.merge(existingMentor);

    assertEquals("Original Name", result.getFullName());
    assertEquals("Original Position", result.getPosition());
    assertEquals("updated@example.com", result.getEmail());
    assertEquals("Original bio", result.getBio());
  }

  @Test
  void testMergeShouldRetainExistingValuesWhenDtoCollectionsAreEmpty() {
    mentorDto =
        MentorDto.mentorDtoBuilder()
            .fullName("Updated Name")
            .images(List.of())
            .network(List.of())
            .spokenLanguages(List.of())
            .build();

    Mentor result = mentorDto.merge(existingMentor);

    assertEquals("Updated Name", result.getFullName());
    assertEquals(1, result.getImages().size());
    assertEquals(1, result.getNetwork().size());
    assertEquals(LINKEDIN, result.getNetwork().getFirst().type());
    assertEquals(2, result.getSpokenLanguages().size());
  }

  @Test
  void testMergeShouldSetOptionalFieldsOnlyWhenAtLeastOneValueExists() {
    Mentor mentorWithNullOptionals =
        Mentor.mentorBuilder()
            .id(1L)
            .fullName("Name")
            .position("Position")
            .email("email@test.com")
            .slackDisplayName("@slack")
            .country(new Country("US", "United States"))
            .images(List.of(new Image("img.jpg", "Image", ImageType.DESKTOP)))
            .profileStatus(ProfileStatus.ACTIVE)
            .bio("Bio")
            .skills(new Skills(5, List.of(), List.of(), List.of()))
            .menteeSection(new MenteeSection("ideal", "additional", null, List.of()))
            .spokenLanguages(null)
            .city(null)
            .companyName(null)
            .network(List.of())
            .build();

    mentorDto = MentorDto.mentorDtoBuilder().fullName("Updated Name").build();

    Mentor result = mentorDto.merge(mentorWithNullOptionals);

    assertNull(result.getCity());
    assertNull(result.getCompanyName());
    assertNotNull(result.getSpokenLanguages());
    assertTrue(result.getSpokenLanguages().isEmpty());
  }

  @Test
  void testMergeShouldThrowExceptionWhenMemberIsNull() {
    mentorDto = MentorDto.mentorDtoBuilder().fullName("Name").build();

    InvalidMentorException exception =
        assertThrows(InvalidMentorException.class, () -> mentorDto.merge(null));

    assertEquals("Cannot merge with null mentor", exception.getMessage());
  }

  @Test
  void testMergeShouldUpdateSkillsCorrectly() {
    Skills newSkills =
        new Skills(
            15,
            List.of(
                new TechnicalAreaProficiency(
                    TechnicalArea.DISTRIBUTED_SYSTEMS, ProficiencyLevel.BEGINNER)),
            List.of(new LanguageProficiency(CodeLanguage.KOTLIN, ProficiencyLevel.BEGINNER)),
            List.of(MentorshipFocusArea.GROW_BEYOND_SENIOR));

    mentorDto = MentorDto.mentorDtoBuilder().skills(newSkills).build();

    Mentor result = mentorDto.merge(existingMentor);

    assertEquals(15, result.getSkills().yearsExperience());
    assertEquals(1, result.getSkills().areas().size());
    assertEquals(
        TechnicalArea.DISTRIBUTED_SYSTEMS, result.getSkills().areas().getFirst().technicalArea());
    assertEquals(1, result.getSkills().languages().size());
    assertEquals(CodeLanguage.KOTLIN, result.getSkills().languages().getFirst().language());
  }

  @Test
  void testMergeShouldUpdateMenteeSectionCorrectly() {
    MenteeSection newMenteeSection =
        new MenteeSection(
            "Updated ideal mentee",
            "Updated additional",
            new LongTermMentorship(2, 8),
            List.of(
                new MentorMonthAvailability(Month.MARCH, 4),
                new MentorMonthAvailability(Month.APRIL, 5)));

    mentorDto = MentorDto.mentorDtoBuilder().menteeSection(newMenteeSection).build();

    Mentor result = mentorDto.merge(existingMentor);

    assertEquals("Updated ideal mentee", result.getMenteeSection().idealMentee());
    assertEquals("Updated additional", result.getMenteeSection().additional());
    assertEquals(2, result.getMenteeSection().getMentorshipTypes().size());
    assertEquals(2, result.getMenteeSection().adHoc().size());
    assertEquals(Month.MARCH, result.getMenteeSection().adHoc().getFirst().month());
    assertEquals(2, result.getMenteeSection().longTerm().numMentee());
    assertEquals(8, result.getMenteeSection().longTerm().hours());
  }

  @Test
  void testMergeShouldThrowExceptionIfMentorIsNull() {
    mentorDto = MentorDto.mentorDtoBuilder().build();

    InvalidMentorException exception =
        assertThrows(InvalidMentorException.class, () -> mentorDto.merge(null));

    assertEquals("Cannot merge with null mentor", exception.getMessage());
  }
}
