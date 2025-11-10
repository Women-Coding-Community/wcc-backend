package com.wcc.platform.domain.template;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RenderedTemplateTest {

  @Test
  @DisplayName("Create RenderedTemplate from Template")
  void from_CreatesRenderedTemplate() {
    Template template = new Template();
    template.setSubject("Test Subject");
    template.setBody("Test Body");

    RenderedTemplate renderedTemplate = RenderedTemplate.from(template);

    assertThat(renderedTemplate.getSubject()).isEqualTo("Test Subject");
    assertThat(renderedTemplate.getBody()).isEqualTo("Test Body");
  }
}
