package com.wcc.platform.domain.platform.mentorship;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class ApplicationStatusTest {

  @Test
  @DisplayName(
      "Given REJECTED status, when checking isNonForwardable, then it should return true")
  void shouldReturnTrueWhenStatusIsRejected() {
    assertThat(ApplicationStatus.REJECTED.isNonForwardable()).isTrue();
  }

  @Test
  @DisplayName(
      "Given MENTOR_DECLINED status, when checking isNonForwardable, then it should return true")
  void shouldReturnTrueWhenStatusIsMentorDeclined() {
    assertThat(ApplicationStatus.MENTOR_DECLINED.isNonForwardable()).isTrue();
  }

  @Test
  @DisplayName("Given DROPPED status, when checking isNonForwardable, then it should return true")
  void shouldReturnTrueWhenStatusIsDropped() {
    assertThat(ApplicationStatus.DROPPED.isNonForwardable()).isTrue();
  }

  @Test
  @DisplayName("Given EXPIRED status, when checking isNonForwardable, then it should return true")
  void shouldReturnTrueWhenStatusIsExpired() {
    assertThat(ApplicationStatus.EXPIRED.isNonForwardable()).isTrue();
  }

  @Test
  @DisplayName("Given PENDING status, when checking isNonForwardable, then it should return false")
  void shouldReturnFalseWhenStatusIsPending() {
    assertThat(ApplicationStatus.PENDING.isNonForwardable()).isFalse();
  }

  @Test
  @DisplayName(
      "Given MENTOR_REVIEWING status, when checking isNonForwardable, then it should return false")
  void shouldReturnFalseWhenStatusIsMentorReviewing() {
    assertThat(ApplicationStatus.MENTOR_REVIEWING.isNonForwardable()).isFalse();
  }

  @Test
  @DisplayName(
      "Given MENTOR_ACCEPTED status, when checking isNonForwardable, then it should return false")
  void shouldReturnFalseWhenStatusIsMentorAccepted() {
    assertThat(ApplicationStatus.MENTOR_ACCEPTED.isNonForwardable()).isFalse();
  }

  @Test
  @DisplayName("Given MATCHED status, when checking isNonForwardable, then it should return false")
  void shouldReturnFalseWhenStatusIsMatched() {
    assertThat(ApplicationStatus.MATCHED.isNonForwardable()).isFalse();
  }

  @Test
  @DisplayName(
      "Given PENDING_MANUAL_MATCH status, when checking isNonForwardable, then it should return false")
  void shouldReturnFalseWhenStatusIsPendingManualMatch() {
    assertThat(ApplicationStatus.PENDING_MANUAL_MATCH.isNonForwardable()).isFalse();
  }

  @Test
  @DisplayName(
      "Given PENDING_MANUAL_MATCH status, when checking isTerminal, then it should return false")
  void shouldReturnFalseForIsTerminalWhenStatusIsPendingManualMatch() {
    assertThat(ApplicationStatus.PENDING_MANUAL_MATCH.isTerminal()).isFalse();
  }

  @ParameterizedTest
  @EnumSource(ApplicationStatus.class)
  @DisplayName("Given any status, when calling fromValue with its value, then it returns the status")
  void shouldReturnCorrectStatusFromValue(final ApplicationStatus status) {
    assertThat(ApplicationStatus.fromValue(status.getValue())).isEqualTo(status);
  }

  @Test
  @DisplayName("Given invalid value, when calling fromValue, then it throws IllegalArgumentException")
  void shouldThrowExceptionForInvalidValue() {
    assertThatThrownBy(() -> ApplicationStatus.fromValue("invalid_status"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Unknown application status: invalid_status");
  }

  @Test
  @DisplayName(
      "Given PENDING_MANUAL_MATCH status, when getting value, then it returns 'pending_manual_match'")
  void shouldReturnCorrectValueForPendingManualMatch() {
    assertThat(ApplicationStatus.PENDING_MANUAL_MATCH.getValue()).isEqualTo("pending_manual_match");
  }
}
