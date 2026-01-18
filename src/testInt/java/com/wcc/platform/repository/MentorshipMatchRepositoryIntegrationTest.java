package com.wcc.platform.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.wcc.platform.domain.platform.mentorship.MentorshipMatch;
import com.wcc.platform.repository.postgres.DefaultDatabaseSetup;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Integration tests for MentorshipMatchRepository with PostgreSQL. Tests match queries and counting
 * operations.
 */
class MentorshipMatchRepositoryIntegrationTest extends DefaultDatabaseSetup {

  @Autowired private MentorshipMatchRepository matchRepository;

  @Test
  @DisplayName(
      "Given no matches exist for mentee, when checking if mentee matched in cycle, then it should return false")
  void shouldReturnFalseWhenMenteeNotMatchedInCycle() {
    final boolean isMatched = matchRepository.isMenteeMatchedInCycle(99L, 1L);

    assertThat(isMatched).isFalse();
  }

  @Test
  @DisplayName("Given non-existent mentor, when counting active mentees, then it should return 0")
  void shouldReturnZeroForNonExistentMentor() {
    final int count = matchRepository.countActiveMenteesByMentorAndCycle(99L, 1L);

    assertThat(count).isZero();
  }

  @Test
  @DisplayName("Given non-existent match ID, when finding by ID, then it should return empty")
  void shouldReturnEmptyForNonExistentMatchId() {
    final Optional<MentorshipMatch> found = matchRepository.findById(99L);

    assertThat(found).isEmpty();
  }

  @Test
  @DisplayName("Given non-existent mentee, when finding active mentor, then it should return empty")
  void shouldReturnEmptyForNonExistentMentee() {
    final Optional<MentorshipMatch> found = matchRepository.findActiveMentorByMentee(99L);

    assertThat(found).isEmpty();
  }

  @Test
  @DisplayName(
      "Given non-existent mentor, when finding active mentees, then it should return empty list")
  void shouldReturnEmptyListForNonExistentMentor() {
    final List<MentorshipMatch> matches = matchRepository.findActiveMenteesByMentor(99L);

    assertThat(matches).isEmpty();
  }

  @Test
  @DisplayName(
      "Given non-existent cycle, when finding matches by cycle, then it should return empty list")
  void shouldReturnEmptyListForNonExistentCycle() {
    final List<MentorshipMatch> matches = matchRepository.findByCycle(99L);

    assertThat(matches).isEmpty();
  }

  @Test
  @DisplayName(
      "Given repository methods are called, when getting all matches, then it should return list")
  void shouldReturnListWhenGettingAllMatches() {
    final List<MentorshipMatch> allMatches = matchRepository.getAll();

    // Should not throw exception, may be empty if no matches exist yet
    assertThat(allMatches).isNotNull();
  }

  @Test
  @DisplayName(
      "Given non-existent combination, when finding by mentor-mentee-cycle, then it should return empty")
  void shouldReturnEmptyForNonExistentCombination() {
    final Optional<MentorshipMatch> found = matchRepository.findByMentorMenteeCycle(99L, 98L, 1L);

    assertThat(found).isEmpty();
  }
}
