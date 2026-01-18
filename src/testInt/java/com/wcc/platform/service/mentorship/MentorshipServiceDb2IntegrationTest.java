package com.wcc.platform.service.mentorship;

import static com.wcc.platform.domain.cms.PageType.MENTORS;
import static com.wcc.platform.factories.SetupMentorFactories.createMentorTest;
import static com.wcc.platform.factories.SetupMentorshipPagesFactories.createMentorsPageTest;
import static org.assertj.core.api.Assertions.assertThat;

import com.wcc.platform.domain.cms.pages.mentorship.MentorsPage;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.PageRepository;
import com.wcc.platform.service.MentorshipService;
import com.wcc.platform.service.PageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test-db2")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Disabled("Temporary disable due to database compatibility issues")
class MentorshipServiceDb2IntegrationTest {

  private final MentorsPage page = createMentorsPageTest(MENTORS.getFileName());
  @Autowired private MentorshipService service;
  @Autowired private MemberRepository repository;
  @Autowired private PageRepository pageRepository;
  @Autowired private PageService pageService;

  /** Factory test. */
  @BeforeEach
  void setUp() {
    var mentor = createMentorTest(null, "mentor postgres", "h2@domain.com");
    var mentorOptional = repository.findByEmail(mentor.getEmail());
    mentorOptional.ifPresent(value -> repository.deleteById(value.getId()));
    pageRepository.deleteById(MENTORS.getId());

    pageService.create(MENTORS, page);
    service.create(mentor);
  }

  @Test
  void testGetPageWithMentor() {
    var mentorsPage = service.getMentorsPage(page);

    assertThat(mentorsPage.openCycle()).isNotNull();
    assertThat(mentorsPage.mentors()).isNotEmpty();
    assertThat(service.getAllMentors()).isNotEmpty();
  }
}
