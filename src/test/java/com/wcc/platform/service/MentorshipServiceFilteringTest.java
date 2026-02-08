package com.wcc.platform.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.cms.attributes.Languages;
import com.wcc.platform.domain.cms.attributes.MentorshipFocusArea;
import com.wcc.platform.domain.cms.attributes.TechnicalArea;
import com.wcc.platform.domain.cms.pages.mentorship.LongTermMentorship;
import com.wcc.platform.domain.cms.pages.mentorship.MenteeSection;
import com.wcc.platform.domain.cms.pages.mentorship.MentorAppliedFilters;
import com.wcc.platform.domain.cms.pages.mentorship.MentorMonthAvailability;
import com.wcc.platform.domain.cms.pages.mentorship.MentorsPage;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycle;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import com.wcc.platform.domain.platform.mentorship.Skills;
import com.wcc.platform.domain.platform.type.MemberType;
import com.wcc.platform.factories.SetupFactories;
import com.wcc.platform.factories.SetupMentorshipPagesFactories;
import com.wcc.platform.repository.MemberProfilePictureRepository;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.MentorRepository;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceFilteringTest {

  @Mock private MentorRepository mentorRepository;
  @Mock private MemberRepository memberRepository;
  @Mock private MemberProfilePictureRepository profilePicRepo;

  private MentorshipService service;
  private Mentor mentor1;
  private MentorsPage mentorsPage;

  @BeforeEach
  void setUp() {
    service = spy(new MentorshipService(mentorRepository, memberRepository, profilePicRepo, 10));
    doReturn(new MentorshipCycle(MentorshipType.AD_HOC, Month.MAY)).when(service).getCurrentCycle();
    mentorsPage = SetupMentorshipPagesFactories.createMentorPageTest();
    mentor1 =
        buildMentor(
            1L,
            "Alice Smith",
            "London",
            "Acme",
            5,
            List.of(TechnicalArea.BACKEND, TechnicalArea.FRONTEND, TechnicalArea.FRONTEND),
            List.of(Languages.JAVA, Languages.PYTHON, Languages.KOTLIN),
            List.of(
                MentorshipFocusArea.GROW_MID_TO_SENIOR, MentorshipFocusArea.SWITCH_CAREER_TO_IT),
            List.of(MentorshipType.LONG_TERM),
            Month.MAY);
    var mentor2 =
        buildMentor(
            2L,
            "Bob Jones",
            "Paris",
            "Globex",
            1,
            List.of(TechnicalArea.FRONTEND),
            List.of(Languages.JAVASCRIPT),
            List.of(MentorshipFocusArea.SWITCH_CAREER_TO_IT),
            List.of(MentorshipType.LONG_TERM),
            Month.MARCH);

    when(mentorRepository.getAll()).thenReturn(List.of(mentor1, mentor2));
  }

  @Test
  @DisplayName(
      "Given mentors page search When all filters are matched "
          + "Then returned only matched mentors")
  void whenApplyAllFiltersInCombinationThenReturnOnlyMatchingMentors() {
    var filters =
        new MentorAppliedFilters(
            "alice",
            List.of(MentorshipType.LONG_TERM),
            3,
            List.of(TechnicalArea.BACKEND),
            List.of(Languages.JAVA),
            List.of(MentorshipFocusArea.GROW_MID_TO_SENIOR));

    var result = service.getMentorsPage(mentorsPage, filters);

    assertEquals(1, result.mentors().size());
    assertEquals(mentor1.getFullName(), result.mentors().getFirst().getFullName());
  }

  @Test
  @DisplayName(
      "Given mentors page search When keyword does not match with any mentor "
          + "Then return empty")
  void givenMentorSearchWhenKeywordDoesNotMatchWithAnyMentorThenReturnEmpty() {
    var filters = new MentorAppliedFilters("John", List.of(), 0, List.of(), List.of(), List.of());

    var result = service.getMentorsPage(mentorsPage, filters);

    assertTrue(result.mentors().isEmpty());
  }

  @Test
  @DisplayName(
      "Given mentors page search When keyword does not match with any mentor "
          + "Then return empty")
  void giveMentorSearchWhenTechnicalAreaDoesNotMatchWithAnyMentorThenReturnEmpty() {
    var filters =
        new MentorAppliedFilters(
            "", List.of(MentorshipType.AD_HOC), 0, List.of(), List.of(), List.of());

    var result = service.getMentorsPage(mentorsPage, filters);

    assertTrue(result.mentors().isEmpty());
  }

  private Mentor buildMentor(
      final Long mentorId,
      final String name,
      final String city,
      final String company,
      final int years,
      final List<TechnicalArea> areas,
      final List<Languages> languages,
      final List<MentorshipFocusArea> focus,
      final List<MentorshipType> types,
      final Month availableMonth) {

    final Member base = SetupFactories.createMemberTest(MemberType.MENTOR);

    // Determine long-term and ad-hoc from types
    final LongTermMentorship longTerm =
        types.contains(MentorshipType.LONG_TERM) ? new LongTermMentorship(1, 4) : null;
    final List<MentorMonthAvailability> adHoc =
        types.contains(MentorshipType.AD_HOC)
            ? List.of(new MentorMonthAvailability(availableMonth, 2))
            : List.of();

    return Mentor.mentorBuilder()
        .id(mentorId)
        .fullName(name)
        .position(base.getPosition())
        .email("test" + mentorId + "@wcc.com")
        .slackDisplayName(base.getSlackDisplayName())
        .country(base.getCountry())
        .city(city)
        .companyName(company)
        .images(base.getImages())
        .network(base.getNetwork())
        .profileStatus(ProfileStatus.ACTIVE)
        .bio("Bio for " + name)
        .spokenLanguages(List.of("English"))
        .skills(new Skills(years, areas, languages, focus))
        .menteeSection(new MenteeSection("ideal", "additional", longTerm, adHoc))
        .build();
  }
}
