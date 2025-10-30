package com.wcc.platform.email.templates.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.wcc.platform.domain.platform.email.templates.RenderedTemplate;
import com.wcc.platform.domain.platform.email.templates.TemplateRegistry;
import com.wcc.platform.domain.platform.email.templates.TemplateRequest;
import com.wcc.platform.domain.platform.email.templates.TemplateSpec;
import com.wcc.platform.domain.platform.email.templates.service.YamlTemplateWriterService;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import com.wcc.platform.domain.platform.type.TemplateType;
import com.wcc.platform.repository.postgres.DefaultDatabaseSetup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TemplateWriterServiceTest extends DefaultDatabaseSetup {
  @Autowired private TemplateRegistry templateRegistry;
  @Autowired private YamlTemplateWriterService writerService;

  @Test
  void registryLoadsFeedbackTemplate() {
    TemplateSpec spec =
        templateRegistry.get(TemplateType.FEEDBACK_FROM_MENTOR, MentorshipType.LONG_TERM);
    assertThat(spec).isNotNull();
    assertThat(spec.getSubject()).contains("Request: feedback");
  }

  @Test
  void rendersFeedbackRequestWithScalars() {
    TemplateRequest req =
        TemplateRequest.builder()
            .templateType(TemplateType.FEEDBACK_FROM_MENTOR)
            .mentorshipType(MentorshipType.LONG_TERM)
            .build()
            .addParam("mentorName", "Alice Mentor")
            .addParam("menteeName", "Bob Mentee")
            .addParam("program", "Data Science Track")
            .addParam("deadline", "2025-11-07");

    RenderedTemplate out = writerService.render(req);

    assertThat(out).isNotNull();
    assertThat(out.getSubject()).contains("Alice Mentor");
    assertThat(out.getBodyHtml()).contains("Alice Mentor");
    assertThat(out.getBodyHtml()).contains("Bob Mentee");
  }
}
