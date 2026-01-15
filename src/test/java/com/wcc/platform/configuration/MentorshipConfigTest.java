package com.wcc.platform.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MentorshipConfigTest {

  @Test
  @DisplayName("Given default configuration When creating MentorshipConfig Then should have default values")
  void shouldHaveDefaultValues() {
    MentorshipConfig config = new MentorshipConfig();

    assertThat(config.getDaysCycleOpen()).isEqualTo(10);
    assertThat(config.getValidation()).isNotNull();
    assertThat(config.getValidation().isEnabled()).isTrue();
  }

  @Test
  @DisplayName("Given custom daysCycleOpen When setting value Then should update correctly")
  void shouldSetDaysCycleOpen() {
    MentorshipConfig config = new MentorshipConfig();
    config.setDaysCycleOpen(15);

    assertThat(config.getDaysCycleOpen()).isEqualTo(15);
  }

  @Test
  @DisplayName("Given validation enabled When disabling Then should update correctly")
  void shouldDisableValidation() {
    MentorshipConfig config = new MentorshipConfig();
    config.getValidation().setEnabled(false);

    assertThat(config.getValidation().isEnabled()).isFalse();
  }

  @Test
  @DisplayName("Given validation disabled When enabling Then should update correctly")
  void shouldEnableValidation() {
    MentorshipConfig config = new MentorshipConfig();
    config.getValidation().setEnabled(false);
    config.getValidation().setEnabled(true);

    assertThat(config.getValidation().isEnabled()).isTrue();
  }

  @Test
  @DisplayName("Given new validation object When setting Then should update correctly")
  void shouldSetNewValidationObject() {
    MentorshipConfig config = new MentorshipConfig();
    MentorshipConfig.Validation newValidation = new MentorshipConfig.Validation();
    newValidation.setEnabled(false);

    config.setValidation(newValidation);

    assertThat(config.getValidation()).isEqualTo(newValidation);
    assertThat(config.getValidation().isEnabled()).isFalse();
  }
}