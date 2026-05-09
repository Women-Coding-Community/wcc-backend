package com.wcc.platform.service;

import com.wcc.platform.domain.cms.attributes.MentorshipFocusArea;
import com.wcc.platform.domain.cms.attributes.TechnicalArea;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.platform.mentorship.LanguageProficiency;
import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleEntity;
import com.wcc.platform.domain.platform.mentorship.TechnicalAreaProficiency;
import com.wcc.platform.domain.platform.mentorship.recommendation.MenteeMatchSuggestion;
import com.wcc.platform.domain.platform.mentorship.recommendation.MentorMatchSuggestion;
import com.wcc.platform.domain.platform.mentorship.recommendation.MentorshipRecommendationResponse;
import com.wcc.platform.repository.MenteeRepository;
import com.wcc.platform.repository.MentorRepository;
import com.wcc.platform.repository.MentorshipCycleRepository;
import com.wcc.platform.repository.MentorshipMatchRepository;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** Service for generating mentorship recommendations based on mentor and mentee criteria. */
@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipRecommendationService {

  private static final int LANGUAGE_SCORE = 50;
  private static final int AREA_SCORE = 30;
  private static final int FOCUS_SCORE = 10;

  private final MentorRepository mentorRepository;
  private final MenteeRepository menteeRepository;
  private final MentorshipMatchRepository matchRepository;
  private final MentorshipCycleRepository cycleRepository;

  /**
   * Get suggested mentee matches for each mentor who has availability.
   *
   * @param cycleId the ID of the mentorship cycle
   * @return recommendation response
   */
  public MentorshipRecommendationResponse getRecommendations(final Long cycleId) {
    final MentorshipCycleEntity cycle = cycleRepository.findById(cycleId).orElse(null);
    if (cycle == null) {
      log.warn("No open mentorship cycle found for recommendations");
      return new MentorshipRecommendationResponse(List.of(), List.of(), List.of());
    }

    final var availableMentors =
        mentorRepository.findAvailableMentors(ProfileStatus.ACTIVE).stream()
            .filter(m -> m.getMenteeSection().longTerm() != null)
            .filter(
                m -> {
                  final int currentMentees =
                      matchRepository.countActiveMenteesByMentorAndCycle(m.getId(), cycleId);
                  return currentMentees < m.getMenteeSection().longTerm().numMentee();
                })
            .toList();

    final var unmatchedMentees =
        menteeRepository.findByStatus(ProfileStatus.ACTIVE).stream()
            .filter(m -> !matchRepository.isMenteeMatchedInCycle(m.getId(), cycleId))
            .toList();

    final List<MentorMatchSuggestion> matchedMentors =
        availableMentors.stream()
            .map(mentor -> buildMentorSuggestion(mentor, unmatchedMentees))
            .filter(s -> !s.mentees().isEmpty())
            .toList();

    final Set<Long> suggestedMenteeIds =
        matchedMentors.stream()
            .flatMap(s -> s.mentees().stream())
            .map(m -> m.mentee().getId())
            .collect(Collectors.toSet());

    final Set<Long> matchedMentorIds =
        matchedMentors.stream().map(s -> s.mentor().getId()).collect(Collectors.toSet());

    if (log.isDebugEnabled()) {
      matchedMentors.forEach(
          s -> log.debug("Mentor ID: {} Suggestions: {}", s.mentor().getId(), s.mentees()));
    }

    final var notMatchedMentors =
        availableMentors.stream()
            .filter(mentor -> !matchedMentorIds.contains(mentor.getId()))
            .toList();

    final var notMatchedMentees =
        unmatchedMentees.stream()
            .filter(mentee -> !suggestedMenteeIds.contains(mentee.getId()))
            .toList();

    return new MentorshipRecommendationResponse(
        matchedMentors, notMatchedMentors, notMatchedMentees);
  }

  private MentorMatchSuggestion buildMentorSuggestion(
      final Mentor mentor, final List<Mentee> unmatchedMentees) {
    final List<MenteeMatchSuggestion> scoredMentees =
        unmatchedMentees.stream()
            .map(mentee -> new MenteeMatchSuggestion(mentee, calculateScore(mentor, mentee)))
            .filter(s -> s.score() > 0)
            .sorted(Comparator.comparingInt(MenteeMatchSuggestion::score).reversed())
            .toList();
    return new MentorMatchSuggestion(mentor, scoredMentees);
  }

  private int calculateScore(final Mentor mentor, final Mentee mentee) {
    return scoreLanguages(mentor, mentee) + scoreAreas(mentor, mentee) + scoreFocus(mentor, mentee);
  }

  private int scoreLanguages(final Mentor mentor, final Mentee mentee) {
    if (mentor.getSkills().languages().isEmpty()) {
      return 0;
    }
    final var mentorLangs =
        mentor.getSkills().languages().stream()
            .map(LanguageProficiency::language)
            .collect(Collectors.toSet());
    return (int)
            mentee.getSkills().languages().stream()
                .map(LanguageProficiency::language)
                .filter(mentorLangs::contains)
                .count()
        * LANGUAGE_SCORE;
  }

  private int scoreAreas(final Mentor mentor, final Mentee mentee) {
    if (mentor.getSkills().areas().isEmpty()) {
      return 0;
    }
    final Set<TechnicalArea> mentorAreas =
        mentor.getSkills().areas().stream()
            .map(TechnicalAreaProficiency::technicalArea)
            .collect(Collectors.toSet());
    return (int)
            mentee.getSkills().areas().stream()
                .map(TechnicalAreaProficiency::technicalArea)
                .filter(mentorAreas::contains)
                .count()
        * AREA_SCORE;
  }

  private int scoreFocus(final Mentor mentor, final Mentee mentee) {
    final var mentorFocus =
        mentor.getSkills().mentorshipFocus().isEmpty()
            ? EnumSet.noneOf(MentorshipFocusArea.class)
            : EnumSet.copyOf(mentor.getSkills().mentorshipFocus());
    return (int) mentee.getSkills().mentorshipFocus().stream().filter(mentorFocus::contains).count()
        * FOCUS_SCORE;
  }
}
