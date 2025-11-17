package com.wcc.platform.controller;

import static com.wcc.platform.factories.MockMvcRequestFactory.postRequest;
import static com.wcc.platform.factories.SetUpTemplateFactories.createTemplateRequest;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wcc.platform.configuration.SecurityConfig;
import com.wcc.platform.configuration.TestConfig;
import com.wcc.platform.domain.exceptions.TemplateValidationException;
import com.wcc.platform.domain.template.RenderedTemplate;
import com.wcc.platform.domain.template.TemplateType;
import com.wcc.platform.service.EmailTemplateService;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@Import({SecurityConfig.class, TestConfig.class})
@WebMvcTest(EmailTemplateController.class)
class EmailTemplateControllerTest {

  private static final String API_EMAIL_TEMP_PREVIEW = "/api/platform/v1/email/template/preview";
  @Autowired private MockMvc mockMvc;
  @MockBean private EmailTemplateService emailTemplateService;

  @Test
  void previewValidRequestReturnsRenderedTemplate() throws Exception {
    var request =
        createTemplateRequest(
            TemplateType.FEEDBACK_MENTOR_ADHOC,
            Map.of("mentorName", "test-mentor-name", "menteeName", "test-mentee-name"));

    RenderedTemplate renderedTemplate = new RenderedTemplate();
    renderedTemplate.setSubject("Feedback from John Doe");
    renderedTemplate.setBody("Dear Jane Smith, Your mentor John Doe has provided feedback.");

    when(emailTemplateService.renderTemplate(eq(TemplateType.FEEDBACK_MENTOR_ADHOC), any()))
        .thenReturn(renderedTemplate);

    mockMvc.perform(postRequest(API_EMAIL_TEMP_PREVIEW, request)).andExpect(status().isCreated());
  }

  @Test
  void previewMissingParametersReturnsBadRequest() throws Exception {
    var request =
        createTemplateRequest(
            TemplateType.FEEDBACK_MENTOR_ADHOC, Map.of("mentorName", "test-mentor-name"));

    var badRequest = new TemplateValidationException("Missing required parameters: [menteeName]");

    when(emailTemplateService.renderTemplate(eq(TemplateType.FEEDBACK_MENTOR_ADHOC), any()))
        .thenThrow(badRequest);

    mockMvc
        .perform(postRequest(API_EMAIL_TEMP_PREVIEW, request).contentType(APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Missing required parameters")));
  }
}
