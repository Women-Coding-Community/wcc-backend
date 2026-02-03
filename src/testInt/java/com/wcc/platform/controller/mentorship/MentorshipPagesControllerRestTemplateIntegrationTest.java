package com.wcc.platform.controller.mentorship;

import static com.wcc.platform.domain.cms.PageType.MENTORS;
import static com.wcc.platform.factories.SetupMentorshipPagesFactories.createMentorsPageTest;
import static org.assertj.core.api.Assertions.assertThat;

import com.wcc.platform.domain.cms.attributes.Languages;
import com.wcc.platform.domain.cms.attributes.MentorshipFocusArea;
import com.wcc.platform.domain.cms.attributes.TechnicalArea;
import com.wcc.platform.domain.cms.pages.mentorship.LongTermMentorship;
import com.wcc.platform.domain.cms.pages.mentorship.MenteeSection;
import com.wcc.platform.domain.cms.pages.mentorship.MentorMonthAvailability;
import com.wcc.platform.domain.cms.pages.mentorship.MentorsPage;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import com.wcc.platform.domain.platform.mentorship.Skills;
import com.wcc.platform.domain.platform.type.MemberType;
import com.wcc.platform.factories.SetupFactories;
import com.wcc.platform.repository.MentorRepository;
import com.wcc.platform.repository.PageRepository;
import com.wcc.platform.repository.postgres.DefaultDatabaseSetup;
import com.wcc.platform.service.PageService;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.UriComponentsBuilder;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class MentorshipPagesControllerRestTemplateIntegrationTest extends DefaultDatabaseSetup {

  private static final String API_MENTORS = "/api/cms/v1/mentorship/mentors";

  @LocalServerPort private int port;

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private PageService pageService;
  @Autowired private PageRepository pageRepository;
  @Autowired private MentorRepository mentorRepository;

  @BeforeEach
  void setUp() {
    pageRepository.deleteById(MENTORS.getId());
    pageService.create(MENTORS, createMentorsPageTest(MENTORS.getFileName()));

    mentorRepository
        .findByEmail("alice.berlin@wcc.com")
        .ifPresent(m -> mentorRepository.deleteById(m.getId()));
    mentorRepository
        .findByEmail("bob.paris@wcc.com")
        .ifPresent(m -> mentorRepository.deleteById(m.getId()));

    seedMentors();
  }

  private void seedMentors() {
    final Member base = SetupFactories.createMemberTest(MemberType.MENTOR);
    var matchedMentor =
        Mentor.mentorBuilder()
            .fullName("Alice Berlin")
            .position(base.getPosition())
            .email("matchedMentor.berlin@wcc.com")
            .slackDisplayName(base.getSlackDisplayName())
            .country(base.getCountry())
            .city("Berlin")
            .companyName("Acme")
            .images(base.getImages())
            .network(base.getNetwork())
            .profileStatus(ProfileStatus.ACTIVE)
            .bio("Bio for Alice in Berlin")
            .spokenLanguages(List.of("English"))
            .skills(
                new Skills(
                    5,
                    List.of(TechnicalArea.BACKEND),
                    List.of(Languages.JAVA),
                    List.of(MentorshipFocusArea.GROW_MID_TO_SENIOR)))
            .menteeSection(
                new MenteeSection(
                    "ideal",
                    "additional",
                    null,
                    List.of(new MentorMonthAvailability(Month.MAY, 2))))
            .build();

    var persistedAlice = mentorRepository.findByEmail(matchedMentor.getEmail()).orElse(null);
    if (persistedAlice == null) {
      mentorRepository.create(matchedMentor);
    }

    // Non-matching mentor
    var bob =
        Mentor.mentorBuilder()
            .fullName("Bob Paris")
            .position(base.getPosition())
            .email("bob.paris@wcc.com")
            .slackDisplayName(base.getSlackDisplayName())
            .country(base.getCountry())
            .city("Paris")
            .companyName("Globex")
            .images(base.getImages())
            .network(base.getNetwork())
            .profileStatus(ProfileStatus.ACTIVE)
            .bio("Bio for Bob in Paris")
            .spokenLanguages(List.of("French"))
            .skills(
                new Skills(
                    1,
                    List.of(TechnicalArea.FRONTEND),
                    List.of(Languages.JAVASCRIPT),
                    List.of(MentorshipFocusArea.SWITCH_CAREER_TO_IT)))
            .menteeSection(
                new MenteeSection(
                    "ideal",
                    "additional",
                    new LongTermMentorship(1, 4),
                    List.of()))
            .build();

    var persistedBob = mentorRepository.findByEmail(bob.getEmail()).orElse(null);
    if (persistedBob == null) {
      mentorRepository.create(bob);
    }
  }

  @Test
  void givenGetMentorsWhenAllFiltersThenReturnsOnlyMatchingMentor() {
    var url =
        UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + API_MENTORS)
            .queryParam("keyword", "Berlin")
            .queryParam("yearsExperience", "3")
            .queryParam("mentorshipTypes", "AD_HOC")
            .queryParam("areas", "BACKEND")
            .queryParam("languages", "JAVA")
            .queryParam("focus", "GROW_MID_TO_SENIOR")
            .build()
            .toUriString();

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-API-KEY", "test-api-key");

    ResponseEntity<MentorsPage> response =
        restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), MentorsPage.class);

    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    MentorsPage body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.mentors()).hasSize(1);
    assertThat(body.mentors().getFirst().getFullName()).isEqualTo("Alice Berlin");
    assertThat(body.filterSection()).isNotNull();
    assertThat(body.openCycle()).isNotNull();
  }
}
