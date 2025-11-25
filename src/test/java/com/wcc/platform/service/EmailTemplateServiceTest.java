package com.wcc.platform.service;

import static com.wcc.platform.factories.SetUpTemplateFactories.createFeedbackTemplate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.exceptions.TemplateValidationException;
import com.wcc.platform.domain.template.RenderedTemplate;
import com.wcc.platform.domain.template.Template;
import com.wcc.platform.domain.template.TemplateType;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;

@ExtendWith(MockitoExtension.class)
class EmailTemplateServiceTest {

  @Mock
  @Qualifier("yamlObjectMapper")
  private ObjectMapper yamlObjectMapper;

  private EmailTemplateService emailTemplateService;

  @BeforeEach
  void setUp() {
    emailTemplateService = new EmailTemplateService(yamlObjectMapper);
  }

  @Test
  void renderTemplateValidParametersSuccess() throws IOException {
    Map<String, String> params = new HashMap<>();
    params.put("mentorName", "Doe");
    params.put("menteeName", "Smith");

    Template template = createFeedbackTemplate();

    Map<String, Template> templates = new HashMap<>();
    templates.put(TemplateType.FEEDBACK_MENTOR_ADHOC.name(), template);

    when(yamlObjectMapper.readValue(any(InputStream.class), any(TypeReference.class)))
        .thenReturn(templates);

    RenderedTemplate result =
        emailTemplateService.renderTemplate(TemplateType.FEEDBACK_MENTOR_ADHOC, params);

    assertThat(result).isNotNull();
    assertThat(result)
        .hasFieldOrPropertyWithValue("subject", "Request: feedback from Doe for Smith");
    assertThat(result).hasFieldOrPropertyWithValue("body", "Hi Doe,Test feedback for the Smith.");
  }

  @Test
  void renderTemplateMissingParametersException() throws IOException {
    // Given
    Map<String, String> params = new HashMap<>();
    params.put("mentorName", "John Doe");

    Template template = createFeedbackTemplate();

    Map<String, Template> templates = new HashMap<>();
    templates.put(TemplateType.FEEDBACK_MENTOR_ADHOC.name(), template);

    when(yamlObjectMapper.readValue(any(InputStream.class), any(TypeReference.class)))
        .thenReturn(templates);

    assertThatThrownBy(
            () -> emailTemplateService.renderTemplate(TemplateType.FEEDBACK_MENTOR_ADHOC, params))
        .isInstanceOf(TemplateValidationException.class)
        .hasMessageContaining("Missing required parameters: [menteeName]");
  }

  @Test
  void invalidTemplateThrowsException() throws IOException {
    Map<String, String> params = new HashMap<>();
    params.put("mentorName", "John Doe");

    when(yamlObjectMapper.readValue(any(InputStream.class), any(TypeReference.class)))
        .thenThrow(new IOException("Template file not found"));

    assertThatThrownBy(
            () -> emailTemplateService.renderTemplate(TemplateType.FEEDBACK_MENTOR_ADHOC, params))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Failed to load template")
        .hasCauseInstanceOf(IOException.class);
  }
}
