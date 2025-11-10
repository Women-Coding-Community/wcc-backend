package com.wcc.platform.domain.template;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TemplateTypeTest {

  @Test
  void getTemplateFile_ReturnsCorrectFileName() {
    TemplateType templateType = TemplateType.FEEDBACK_FROM_MENTOR_ADHOC;

    String templateFile = templateType.getTemplateFile();

    assertThat(templateFile).isEqualTo("feedback_mentor_adhoc.yml");
  }
}
