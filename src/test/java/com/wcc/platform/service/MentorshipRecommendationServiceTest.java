package com.wcc.platform.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.cms.attributes.CodeLanguage;
import com.wcc.platform.domain.cms.attributes.MentorshipFocusArea;
import com.wcc.platform.domain.cms.attributes.ProficiencyLevel;
import com.wcc.platform.domain.cms.attributes.TechnicalArea;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.platform.mentorship.LanguageProficiency;
import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleEntity;
import com.wcc.platform.domain.platform.mentorship.Skills;
import com.wcc.platform.domain.platform.mentorship.TechnicalAreaProficiency;
import com.wcc.platform.domain.platform.mentorship.recommendation.MentorshipRecommendationResponse;
import com.wcc.platform.factories.SetupMenteeFactories;
import com.wcc.platform.factories.SetupMentorFactories;
import com.wcc.platform.repository.MenteeRepository;
import com.wcc.platform.repository.MentorRepository;
import com.wcc.platform.repository.MentorshipCycleRepository;
import com.wcc.platform.repository.MentorshipMatchRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MentorshipRecommendationServiceTest {

  @Mock private MentorRepository mentorRepository;
  @Mock private MenteeRepository menteeRepository;
  @Mock private MentorshipMatchRepository matchRepository;
  @Mock private MentorshipCycleRepository cycleRepository;

  @InjectMocks private MentorshipRecommendationService service;

  @BeforeEach
  void setUp() {
    final var cycle = MentorshipCycleEntity.builder().cycleId(1L).build();
    when(cycleRepository.findById(1L)).thenReturn(Optional.of(cycle));
  }

  @Test
  @DisplayName(
      "Given active mentor and mentee with matching skills, when getRecommendations is called, then return suggestions with score")
  void shouldReturnRecommendationsWhenSkillsMatch() {
    // Mentor with Java and Backend
    Mentor mentorBase = SetupMentorFactories.createMentorTest(1L, "Mentor Java", "mentor@test.com");
    Mentor mentor =
        Mentor.mentorBuilder()
            .id(mentorBase.getId())
            .fullName(mentorBase.getFullName())
            .email(mentorBase.getEmail())
            .profileStatus(ProfileStatus.ACTIVE)
            .menteeSection(mentorBase.getMenteeSection())
            .bio(mentorBase.getBio())
            .skills(
                new Skills(
                    5,
                    List.of(
                        new TechnicalAreaProficiency(
                            TechnicalArea.BACKEND, ProficiencyLevel.ADVANCED)),
                    List.of(new LanguageProficiency(CodeLanguage.JAVA, ProficiencyLevel.ADVANCED)),
                    List.of(MentorshipFocusArea.GROW_BEGINNER_TO_MID)))
            .build();

    // Mentee with Java and Backend
    Mentee menteeBase =
        SetupMenteeFactories.createMenteeTest(10L, "Mentee Java", "mentee@test.com");
    Mentee mentee =
        Mentee.menteeBuilder()
            .id(menteeBase.getId())
            .fullName(menteeBase.getFullName())
            .email(menteeBase.getEmail())
            .profileStatus(ProfileStatus.ACTIVE)
            .bio(menteeBase.getBio())
            .spokenLanguages(List.of("English"))
            .availableHsMonth(menteeBase.getAvailableHsMonth())
            .skills(
                new Skills(
                    1,
                    List.of(
                        new TechnicalAreaProficiency(
                            TechnicalArea.BACKEND, ProficiencyLevel.BEGINNER)),
                    List.of(new LanguageProficiency(CodeLanguage.JAVA, ProficiencyLevel.BEGINNER)),
                    List.of(MentorshipFocusArea.GROW_BEGINNER_TO_MID)))
            .build();

    when(mentorRepository.findAvailableMentors(ProfileStatus.ACTIVE)).thenReturn(List.of(mentor));
    when(menteeRepository.findByStatus(ProfileStatus.ACTIVE)).thenReturn(List.of(mentee));
    when(matchRepository.countActiveMenteesByMentorAndCycle(anyLong(), anyLong())).thenReturn(0);
    when(matchRepository.isMenteeMatchedInCycle(anyLong(), anyLong())).thenReturn(false);

    MentorshipRecommendationResponse response = service.getRecommendations(1L);

    assertThat(response.matchedMentors()).hasSize(1);
    assertThat(response.matchedMentors().getFirst().mentor().getId()).isEqualTo(1L);
    assertThat(response.matchedMentors().getFirst().mentees()).hasSize(1);
    assertThat(response.matchedMentors().getFirst().mentees().getFirst().mentee().getId())
        .isEqualTo(10L);
    assertThat(response.matchedMentors().getFirst().mentees().getFirst().score()).isGreaterThan(0);
    assertThat(response.notMatchedMentors()).isEmpty();
    assertThat(response.notMatchedMentees()).isEmpty();
  }

  @Test
  @DisplayName(
      "Given mentor at capacity, when getRecommendations is called, then mentor is not included in suggestions")
  void shouldNotRecommendWhenMentorAtCapacity() {
    Mentor mentorBase = SetupMentorFactories.createMentorTest(1L, "Mentor Full", "mentor@test.com");
    Mentor mentor =
        Mentor.mentorBuilder()
            .id(mentorBase.getId())
            .fullName(mentorBase.getFullName())
            .email(mentorBase.getEmail())
            .profileStatus(ProfileStatus.ACTIVE)
            .bio(mentorBase.getBio())
            .skills(mentorBase.getSkills())
            .menteeSection(mentorBase.getMenteeSection())
            .build();

    // Set capacity to 1
    mentor
        .getMenteeSection()
        .longTerm()
        .numMentee(); // this is record, so it might be final, let's see.

    when(mentorRepository.findAvailableMentors(ProfileStatus.ACTIVE)).thenReturn(List.of(mentor));
    when(menteeRepository.findByStatus(ProfileStatus.ACTIVE)).thenReturn(List.of());

    // Assume capacity is 1 from SetupMentorFactories and it already has 1 match
    int capacity = mentor.getMenteeSection().longTerm().numMentee();
    when(matchRepository.countActiveMenteesByMentorAndCycle(1L, 1L)).thenReturn(capacity);

    MentorshipRecommendationResponse response = service.getRecommendations(1L);

    assertThat(response.matchedMentors()).isEmpty();
    assertThat(response.notMatchedMentors()).isEmpty();
  }

  @Test
  @DisplayName(
      "Given mentee already matched, when getRecommendations is called, then mentee is not recommended")
  void shouldNotRecommendWhenMenteeAlreadyMatched() {
    Mentor mentor = SetupMentorFactories.createMentorTest(1L, "Mentor Java", "mentor@test.com");
    Mentee mentee = SetupMenteeFactories.createMenteeTest(10L, "Mentee Java", "mentee@test.com");

    when(mentorRepository.findAvailableMentors(ProfileStatus.ACTIVE)).thenReturn(List.of(mentor));
    when(menteeRepository.findByStatus(ProfileStatus.ACTIVE)).thenReturn(List.of(mentee));
    when(matchRepository.countActiveMenteesByMentorAndCycle(1L, 1L)).thenReturn(0);
    when(matchRepository.isMenteeMatchedInCycle(10L, 1L)).thenReturn(true);

    MentorshipRecommendationResponse response = service.getRecommendations(1L);

    assertThat(response.matchedMentors()).isEmpty();
    assertThat(response.notMatchedMentors()).extracting(Mentor::getId).containsExactly(1L);
    assertThat(response.notMatchedMentees()).isEmpty();
  }

  @Test
  @DisplayName(
      "Given no skills match, when getRecommendations is called, then return empty matches")
  void shouldReturnEmptyWhenNoSkillsMatch() {
    Mentor mentorBase = SetupMentorFactories.createMentorTest(1L, "Mentor Java", "mentor@test.com");
    Mentor mentor =
        Mentor.mentorBuilder()
            .id(mentorBase.getId())
            .fullName(mentorBase.getFullName())
            .email(mentorBase.getEmail())
            .profileStatus(ProfileStatus.ACTIVE)
            .menteeSection(mentorBase.getMenteeSection())
            .bio(mentorBase.getBio())
            .skills(
                new Skills(
                    5,
                    List.of(
                        new TechnicalAreaProficiency(
                            TechnicalArea.BACKEND, ProficiencyLevel.ADVANCED)),
                    List.of(new LanguageProficiency(CodeLanguage.JAVA, ProficiencyLevel.ADVANCED)),
                    List.of(MentorshipFocusArea.GROW_BEGINNER_TO_MID)))
            .build();

    Mentee menteeBase =
        SetupMenteeFactories.createMenteeTest(10L, "Mentee Python", "mentee@test.com");
    Mentee mentee =
        Mentee.menteeBuilder()
            .id(menteeBase.getId())
            .fullName(menteeBase.getFullName())
            .email(menteeBase.getEmail())
            .profileStatus(ProfileStatus.ACTIVE)
            .bio(menteeBase.getBio())
            .spokenLanguages(List.of("English"))
            .availableHsMonth(menteeBase.getAvailableHsMonth())
            .skills(
                new Skills(
                    1,
                    List.of(
                        new TechnicalAreaProficiency(
                            TechnicalArea.FRONTEND, ProficiencyLevel.BEGINNER)),
                    List.of(
                        new LanguageProficiency(CodeLanguage.PYTHON, ProficiencyLevel.BEGINNER)),
                    List.of(MentorshipFocusArea.SWITCH_TO_MANAGEMENT)))
            .build();

    when(mentorRepository.findAvailableMentors(ProfileStatus.ACTIVE)).thenReturn(List.of(mentor));
    when(menteeRepository.findByStatus(ProfileStatus.ACTIVE)).thenReturn(List.of(mentee));
    when(matchRepository.countActiveMenteesByMentorAndCycle(1L, 1L)).thenReturn(0);
    when(matchRepository.isMenteeMatchedInCycle(10L, 1L)).thenReturn(false);

    MentorshipRecommendationResponse response = service.getRecommendations(1L);

    assertThat(response.matchedMentors()).isEmpty();
    assertThat(response.notMatchedMentors()).extracting(Mentor::getId).containsExactly(1L);
    assertThat(response.notMatchedMentees()).extracting(Mentee::getId).containsExactly(10L);
  }
}
