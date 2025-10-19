package com.wcc.platform.service.mentorship;

import static com.wcc.platform.domain.cms.PageType.MENTORS;
import static com.wcc.platform.factories.SetupMentorshipFactories.createMentorTest;
import static com.wcc.platform.factories.SetupMentorshipFactories.createMentorsPageTest;
import static org.assertj.core.api.Assertions.assertThat;

import com.wcc.platform.domain.cms.pages.mentorship.MentorsPage;
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

  @Autowired private MentorshipService service;
  @Autowired private MentorRepository repository;
  @Autowired private PageService pageService;
  @Autowired private PageRepository pageRepository;

  @BeforeEach
  void setUp() {
    var mentor = createMentorTest(4L, "mentor postgres", "postgres@domain.com");
    var mentorOptional = repository.findByEmail(mentor.getEmail());
    mentorOptional.ifPresent(value -> repository.deleteById(value.getId()));
    pageRepository.deleteById(MENTORS.getId());
    pageService.create(MENTORS, page);
    repository.deleteById(mentor.getId());
    service.create(mentor);
  }

  @Test
  void testGetPageWithMentor() {
    var mentorsPage = service.getMentorsPage(page);

    assertThat(service.getAllMentors()).isNotEmpty();
    assertThat(mentorsPage.openCycle()).isNotNull();
    var mentors = mentorsPage.mentors();
    assertThat(mentors).isNotEmpty();
  }
}
