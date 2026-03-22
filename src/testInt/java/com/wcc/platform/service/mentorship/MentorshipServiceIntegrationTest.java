package com.wcc.platform.service.mentorship;

import static com.wcc.platform.domain.cms.PageType.MENTORS;
import static com.wcc.platform.factories.SetupMentorFactories.createMentorTest;
import static com.wcc.platform.factories.SetupMentorFactories.createResourceTest;
import static com.wcc.platform.factories.SetupMentorshipPagesFactories.createMentorsPageTest;
import static org.assertj.core.api.Assertions.assertThat;

import com.wcc.platform.domain.cms.attributes.ImageType;
import com.wcc.platform.domain.cms.pages.mentorship.MentorsPage;
import com.wcc.platform.domain.platform.member.Member;
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

  private Mentor setupMentor;

  @BeforeEach
  void setUp() {
    setupMentor = createMentorTest(4L, "mentor postgres", "postgres@domain.com");
    cleanupMentor(setupMentor);
    pageRepository.deleteById(MENTORS.getId());
    pageService.create(MENTORS, page);
    service.create(setupMentor);
  }

  @AfterEach
  void tearDown() {
    cleanupMentor(setupMentor);
  }

  @Test
  @DisplayName(
      "Given mentors page and mentor in database, when getMentorsPage is called, then it should"
          + " return page with mentors and open cycle")
  void shouldReturnMentorsPageWithMentorsAndOpenCycle() {
    var mentorsPage = service.getMentorsPage(page);

    assertThat(service.getAllMentors()).isNotEmpty();
    assertThat(mentorsPage.openCycle()).isNotNull();
    var mentors = mentorsPage.mentors();
    assertThat(mentors).isNotEmpty();
  }

  @Test
  @DisplayName(
      "Given mentor with uploaded profile picture in database, when getAllMentors is called, then"
          + " DTO images should contain profile picture with correct URL")
  void shouldFetchMentorsWithProfilePicturesFromDatabase() {
    var mentor = createMentorTest(5L, "mentor with picture", "picture@domain.com");
    memberRepository.deleteByEmail(mentor.getEmail());
    repository.deleteById(mentor.getId());
    var createdMentor = repository.create(mentor);

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

    var mentors = service.getAllMentors();

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
    var mentor = createMentorTest(6L, "mentor without picture", "nopicture@domain.com");
    memberRepository.deleteByEmail(mentor.getEmail());
    repository.deleteById(mentor.getId());
    var createdMentor = repository.create(mentor);

    var mentors = service.getAllMentors();

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

    // Create a mentor with the same email
    final Mentor mentor =
        createMentorTest(null, "Mentor From Existing Member", "existing-mentor-member@test.com");

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
    var mentor = createMentorTest(7L, "mentor with pronouns", "pronouns@domain.com");
    mentor =
        Mentor.mentorBuilder()
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
            .pronouns("they/them")
            .pronounCategory(com.wcc.platform.domain.cms.attributes.PronounCategory.NEUTRAL)
            .profileStatus(mentor.getProfileStatus())
            .spokenLanguages(mentor.getSpokenLanguages())
            .bio(mentor.getBio())
            .skills(mentor.getSkills())
            .menteeSection(mentor.getMenteeSection())
            .build();

    memberRepository.deleteByEmail(mentor.getEmail());
    repository.deleteById(mentor.getId());
    var createdMentor = repository.create(mentor);

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

    var mentors = service.getAllMentors();

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
    memberRepository.deleteByEmail(mentor.getEmail());
    repository.deleteById(mentor.getId());
  }
}
