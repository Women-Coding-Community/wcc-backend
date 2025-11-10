package com.wcc.platform.controller;

import static com.wcc.platform.factories.MockMvcRequestFactory.postRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.configuration.SecurityConfig;
import com.wcc.platform.domain.template.RenderedTemplate;
import com.wcc.platform.domain.template.TemplateRequest;
import com.wcc.platform.domain.template.TemplateType;
import com.wcc.platform.service.EmailTemplateService;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@Import(SecurityConfig.class)
@WebMvcTest(EmailTemplateController.class)
class EmailTemplateControllerTest {

  private static final String API_EMAIL_TEMP_PREVIEW = "/api/platform/v1/email/template/preview";
  @Autowired private ObjectMapper objectMapper;
  @Autowired private MockMvc mockMvc;
  @MockBean private EmailTemplateService emailTemplateService;

  @Test
  void preview_ValidRequest_ReturnsRenderedTemplate() throws Exception {
    TemplateRequest request = new TemplateRequest();
    request.setTemplateType(TemplateType.FEEDBACK_FROM_MENTOR_ADHOC);
    Map<String, String> params = new HashMap<>();
    params.put("mentorName", "John Doe");
    params.put("menteeName", "Jane Smith");
    request.setParams(params);

    RenderedTemplate renderedTemplate = new RenderedTemplate();
    renderedTemplate.setSubject("Feedback from John Doe");
    renderedTemplate.setBody("Dear Jane Smith, Your mentor John Doe has provided feedback.");

    when(emailTemplateService.renderTemplate(eq(TemplateType.FEEDBACK_FROM_MENTOR_ADHOC), any()))
        .thenReturn(renderedTemplate);

    mockMvc.perform(postRequest(API_EMAIL_TEMP_PREVIEW, request)).andExpect(status().isCreated());
  }

  /* @Test
  void preview_MissingParameters_ReturnsBadRequest() throws Exception {
    TemplateRequest request = new TemplateRequest();
    request.setTemplateType(TemplateType.FEEDBACK_FROM_MENTOR_ADHOC);
    Map<String, String> params = new HashMap<>();
    params.put("mentorName", "John Doe");
    request.setParams(params);

    when(emailTemplateService.renderTemplate(eq(TemplateType.FEEDBACK_FROM_MENTOR_ADHOC), any()))
        .thenThrow(new TemplateValidationException("Missing required parameters: [menteeName]"));

    mockMvc
        .perform(postRequest(API_EMAIL_TEMP_PREVIEW, request))
        .andExpect(status().isBadRequest());
  }

  @Test
  void preview_InvalidRequest_ReturnsBadRequest() throws Exception {
    // When/Then
    mockMvc
        .perform(
            post("/api/platform/v1/email/template/preview")
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
        .andExpect(status().isBadRequest());
  }*/
}
