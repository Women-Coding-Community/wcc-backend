package com.wcc.platform.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.template.TemplateType;
import com.wcc.platform.repository.postgres.DefaultDatabaseSetup;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class EmailTemplateServiceTest extends DefaultDatabaseSetup {

  @Autowired private EmailTemplateService templateService;

  @Autowired
  @Qualifier("yamlObjectMapper")
  private ObjectMapper yamlObjectMapper;

  @Test
  void rendersTemplateWithParams() {
    var templateType = TemplateType.FEEDBACK_MENTOR_ADHOC;
    var parameters =
        Map.of(
            "mentorName", "Alice Mentor",
            "menteeName", "Bob Mentee",
            "program", "Mentorship",
            "deadline", "2025-12-01",
            "teamEmailSignature", "Best regards");

    var out = templateService.renderTemplate(templateType, parameters);

    assertThat(out).isNotNull();
    assertThat(out.subject()).contains("Alice Mentor");
    assertThat(out.body()).contains("Mentorship");
  }
}
