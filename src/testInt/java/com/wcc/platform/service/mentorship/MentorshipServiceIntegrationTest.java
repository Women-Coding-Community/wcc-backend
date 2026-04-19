package com.wcc.platform.service.mentorship;

import static com.wcc.platform.domain.cms.PageType.MENTORS;
import static com.wcc.platform.factories.SetupMentorFactories.createMentorTestWithoutImages;
import static com.wcc.platform.factories.SetupMentorFactories.createResourceTest;
import static com.wcc.platform.factories.SetupMentorshipPagesFactories.createMentorsPageTest;
import static org.assertj.core.api.Assertions.assertThat;

import com.wcc.platform.domain.cms.attributes.ImageType;
import com.wcc.platform.domain.cms.pages.mentorship.MentorsPage;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.domain.platform.type.ResourceType;
import com.wcc.platform.domain.resource.MemberProfilePicture;
import com.wcc.platform.repository.MemberProfilePictureRepository;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.MentorRepository;
import com.wcc.platform.repository.PageRepository;
import com.wcc.platform.repository.ResourceRepository;
import com.wcc.platform.repository.postgres.DefaultDatabaseSetup;
import com.wcc.platform.service.MentorshipService;
import com.wcc.platform.service.PageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class MentorshipServiceIntegrationTest extends DefaultDatabaseSetup {

  private final MentorsPage page = createMentorsPageTest(MENTORS.getFileName());

  @Autowired private MentorshipService service;
  @Autowired private MentorRepository repository;
  @Autowired private MemberRepository memberRepository;
  @Autowired private PageService pageService;
  @Autowired private PageRepository pageRepository;
  @Autowired private ResourceRepository resourceRepository;
  @Autowired private MemberProfilePictureRepository profilePicRepository;
  @Autowired private JdbcTemplate jdbcTemplate;

  private Mentor setupMentor;

  @BeforeEach
  void setUp() {
    jdbcTemplate.update("DELETE FROM member_profile_picture");

    setupMentor = createMentorTestWithoutImages(4L, "mentor postgres", "postgres@domain.com");
    cleanupMentor(setupMentor);
    pageRepository.deleteById(MENTORS.getId());
    pageService.create(MENTORS, page);
    setupMentor = service.create(setupMentor);
    repository.updateProfileStatus(setupMentor.getId(), ProfileStatus.ACTIVE);
  }

  @AfterEach
  void tearDown() {
    if (setupMentor != null) {
      cleanupMentor(setupMentor);
    }
  }

  @Test
  @DisplayName(
      "Given mentors page and mentor in database, when getMentorsPage is called, then it should"
          + " return page with mentors and open cycle")
  void shouldReturnMentorsPageWithMentorsAndOpenCycle() {
    var mentorsPage = service.getMentorsPage(page);

    assertThat(service.getAllActiveMentors()).isNotEmpty();
    assertThat(mentorsPage.openCycle()).isNotNull();
    var mentors = mentorsPage.mentors();
    assertThat(mentors).isNotEmpty();
  }

  @Test
  @DisplayName(
      "Given mentor with uploaded profile picture in database, when getAllMentors is called, then"
          + " DTO images should contain profile picture with correct URL")
  void shouldFetchMentorsWithProfilePicturesFromDatabase() {
    var mentor = createMentorTestWithoutImages(5L, "mentor with picture", "picture@domain.com");

    memberRepository.deleteByEmail(mentor.getEmail());
    repository.deleteById(mentor.getId());
    var createdMentor = repository.create(mentor);
    repository.updateProfileStatus(createdMentor.getId(), ProfileStatus.ACTIVE);

    var resource =
        createResourceTest().toBuilder()
            .resourceType(ResourceType.PROFILE_PICTURE)
            .driveFileLink("https://drive.google.com/file/d/integration-test-file/view")
            .build();
    var createdResource = resourceRepository.create(resource);

    var profilePicture =
        MemberProfilePicture.builder()
            .memberId(createdMentor.getId())
            .resourceId(createdResource.getId())
            .resource(createdResource)
            .build();
    profilePicRepository.create(profilePicture);

    var mentors = service.getAllActiveMentors();

    var mentorWithPicture =
        mentors.stream()
            .filter(m -> m.getId().equals(createdMentor.getId()))
            .findFirst()
            .orElseThrow();

    assertThat(mentorWithPicture.getImages()).hasSize(1);
    assertThat(mentorWithPicture.getImages().get(0).path())
        .isEqualTo(createdResource.getDriveFileLink());
    assertThat(mentorWithPicture.getImages().get(0).type()).isEqualTo(ImageType.DESKTOP);

    profilePicRepository.deleteByMemberId(createdMentor.getId());
    resourceRepository.deleteById(createdResource.getId());
    repository.deleteById(createdMentor.getId());
    memberRepository.deleteById(createdMentor.getId());
  }

  @Test
  @DisplayName(
      "Given mentor without profile picture in database, when getAllMentors is called, then images"
          + " list should be empty")
  void shouldHandleMentorsWithoutProfilePicturesInDatabase() {
    var mentor =
        createMentorTestWithoutImages(6L, "mentor without picture", "nopicture@domain.com");

    memberRepository.deleteByEmail(mentor.getEmail());
    repository.deleteById(mentor.getId());
    var createdMentor = repository.create(mentor);
    repository.updateProfileStatus(createdMentor.getId(), ProfileStatus.ACTIVE);

    var mentors = service.getAllActiveMentors();

    var mentorWithoutPicture =
        mentors.stream()
            .filter(m -> m.getId().equals(createdMentor.getId()))
            .findFirst()
            .orElseThrow();

    assertThat(mentorWithoutPicture.getImages()).isNullOrEmpty();

    repository.deleteById(createdMentor.getId());
    memberRepository.deleteById(createdMentor.getId());
  }

  @Test
  @DisplayName(
      "Given existing member with email, when creating mentor with same email, then it should use existing member")
  void shouldUseExistingMemberWhenMentorEmailAlreadyExists() {
    // Create a regular member first
    final Member existingMember =
        Member.builder()
            .fullName("Existing Member")
            .email("existing-mentor-member@test.com")
            .position("Software Engineer")
            .slackDisplayName("@existing-mentor")
            .country(new com.wcc.platform.domain.cms.attributes.Country("US", "United States"))
            .city("New York")
            .companyName("Tech Corp")
            .memberTypes(java.util.List.of(com.wcc.platform.domain.platform.type.MemberType.MEMBER))
            .images(java.util.List.of())
            .network(java.util.List.of())
            .build();

    final Member savedMember = memberRepository.create(existingMember);

    final Mentor mentor =
        createMentorTestWithoutImages(
            null, "Mentor From Existing Member", "existing-mentor-member@test.com");

    // Should successfully create mentor using existing member's ID
    final Mentor savedMentor = service.create(mentor);

    assertThat(savedMentor).isNotNull();
    assertThat(savedMentor.getId()).isEqualTo(savedMember.getId());
    assertThat(savedMentor.getEmail()).isEqualTo("existing-mentor-member@test.com");

    // Cleanup
    repository.deleteById(savedMentor.getId());
    memberRepository.deleteById(savedMember.getId());
  }

  @Test
  @DisplayName(
      "Given mentor with pronouns in database, when getAllMentors is called, then DTO should"
          + " contain pronouns and pronoun category")
  void shouldReturnMentorsWithPronounsFromDatabase() {
    var baseMentor =
        createMentorTestWithoutImages(7L, "mentor with pronouns", "pronouns@domain.com");

    // Only customize pronouns and pronoun category
    var mentor =
        Mentor.mentorBuilder()
            .id(baseMentor.getId())
            .fullName(baseMentor.getFullName())
            .position(baseMentor.getPosition())
            .email(baseMentor.getEmail())
            .slackDisplayName(baseMentor.getSlackDisplayName())
            .country(baseMentor.getCountry())
            .city(baseMentor.getCity())
            .companyName(baseMentor.getCompanyName())
            .images(baseMentor.getImages())
            .network(baseMentor.getNetwork())
            .pronouns("they/them")
            .pronounCategory(com.wcc.platform.domain.cms.attributes.PronounCategory.NEUTRAL)
            .profileStatus(baseMentor.getProfileStatus())
            .skills(baseMentor.getSkills())
            .spokenLanguages(baseMentor.getSpokenLanguages())
            .bio(baseMentor.getBio())
            .menteeSection(baseMentor.getMenteeSection())
            .feedbackSection(baseMentor.getFeedbackSection())
            .resources(baseMentor.getResources())
            .isWomen(baseMentor.getIsWomen())
            .calendlyLink(baseMentor.getCalendlyLink())
            .acceptMale(baseMentor.getAcceptMale())
            .acceptPromotion(baseMentor.getAcceptPromotion())
            .build();

    memberRepository.deleteByEmail(mentor.getEmail());
    repository.deleteById(mentor.getId());
    var createdMentor = repository.create(mentor);
    repository.updateProfileStatus(createdMentor.getId(), ProfileStatus.ACTIVE);

    var resource =
        createResourceTest().toBuilder()
            .resourceType(ResourceType.PROFILE_PICTURE)
            .driveFileLink("https://drive.google.com/file/d/pronouns-test-file/view")
            .build();
    var createdResource = resourceRepository.create(resource);

    var profilePicture =
        MemberProfilePicture.builder()
            .memberId(createdMentor.getId())
            .resourceId(createdResource.getId())
            .resource(createdResource)
            .build();
    profilePicRepository.create(profilePicture);

    var mentors = service.getAllActiveMentors();

    var mentorResult =
        mentors.stream()
            .filter(m -> m.getId().equals(createdMentor.getId()))
            .findFirst()
            .orElseThrow();

    assertThat(mentorResult.getPronouns()).isEqualTo("they/them");
    assertThat(mentorResult.getPronounCategory())
        .isEqualTo(com.wcc.platform.domain.cms.attributes.PronounCategory.NEUTRAL);

    profilePicRepository.deleteByMemberId(createdMentor.getId());
    resourceRepository.deleteById(createdResource.getId());
    repository.deleteById(createdMentor.getId());
    memberRepository.deleteById(createdMentor.getId());
  }

  private void cleanupMentor(final Mentor mentor) {
    if (mentor != null && mentor.getId() != null) {
      profilePicRepository.deleteByMemberId(mentor.getId());
    }
    memberRepository.deleteByEmail(mentor.getEmail());
    repository.deleteById(mentor.getId());
  }
}
