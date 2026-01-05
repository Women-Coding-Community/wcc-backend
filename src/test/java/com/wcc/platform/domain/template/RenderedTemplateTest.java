package com.wcc.platform.domain.template;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RenderedTemplateTest {

  @Test
  @DisplayName("Create RenderedTemplate from Template")
  void createsRenderedTemplate() {
    Template template = new Template("Test Subject", "Test Body");

    RenderedTemplate renderedTemplate = RenderedTemplate.from(template);

    assertThat(renderedTemplate).hasFieldOrPropertyWithValue("subject", "Test Subject");
    assertThat(renderedTemplate).hasFieldOrPropertyWithValue("body", "Test Body");
  }
}
