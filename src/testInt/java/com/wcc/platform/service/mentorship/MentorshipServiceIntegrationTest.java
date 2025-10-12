package com.wcc.platform.service.mentorship;

import static com.wcc.platform.domain.cms.PageType.MENTORS;
import static com.wcc.platform.factories.SetupMentorshipFactories.createMentorsPageTest;
import static org.assertj.core.api.Assertions.assertThat;

import com.wcc.platform.domain.cms.pages.mentorship.MentorsPage;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.factories.SetupMentorshipFactories;
import com.wcc.platform.repository.MentorRepository;
import com.wcc.platform.repository.PageRepository;
import com.wcc.platform.repository.postgres.DefaultDatabaseSetup;
import com.wcc.platform.service.MentorshipService;
import com.wcc.platform.service.PageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class MentorshipServiceIntegrationTest extends DefaultDatabaseSetup {

  private final MentorsPage page = createMentorsPageTest(MENTORS.getFileName());
  private final Mentor mentor = SetupMentorshipFactories.createMentorTest();

  @Autowired private MentorshipService service;
  @Autowired private MentorRepository repository;
  @Autowired private PageService pageService;
  @Autowired private PageRepository pageRepository;

  @BeforeEach
  void setUp() {
    var mentorOptional = repository.findByEmail(mentor.getEmail());
    mentorOptional.ifPresent(value -> repository.deleteById(value.getId()));
    pageRepository.deleteById(MENTORS.getId());
    pageService.create(MENTORS, page);
    service.create(mentor);
  }

  @Test
  void testGetPageWithMentor() {
    var mentorsPage = service.getMentorsPage(page);

    assertThat(service.getAllMentors()).hasSize(1);
    assertThat(mentorsPage.openCycle()).isNotNull();
    var mentors = mentorsPage.mentors();
    assertThat(mentors).hasSize(1);
    assertThat(mentors.getFirst().getFullName()).isEqualTo(mentor.getFullName());
  }
}
