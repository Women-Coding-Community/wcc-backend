package com.wcc.platform.service.mentorship;

import static com.wcc.platform.domain.cms.PageType.MENTORS;
import static com.wcc.platform.factories.SetupMentorFactories.createMentorTest;
import static com.wcc.platform.factories.SetupMentorFactories.createResourceTest;
import static com.wcc.platform.factories.SetupMentorshipPagesFactories.createMentorsPageTest;
import static org.assertj.core.api.Assertions.assertThat;

import com.wcc.platform.domain.cms.attributes.ImageType;
import com.wcc.platform.domain.cms.pages.mentorship.MentorsPage;
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

  private void cleanupMentor(final Mentor mentor) {
    memberRepository.deleteByEmail(mentor.getEmail());
    repository.deleteById(mentor.getId());
  }
}
