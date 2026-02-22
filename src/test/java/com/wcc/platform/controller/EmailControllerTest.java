package com.wcc.platform.controller;

import static com.wcc.platform.domain.template.TemplateType.FEEDBACK_MENTOR_ADHOC;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.configuration.SecurityConfig;
import com.wcc.platform.configuration.TestConfig;
import com.wcc.platform.domain.email.EmailRequest;
import com.wcc.platform.domain.email.EmailResponse;
import com.wcc.platform.domain.email.TemplateEmailRequest;
import com.wcc.platform.domain.exceptions.EmailSendException;
import com.wcc.platform.service.EmailService;
import com.wcc.platform.service.EmailTemplateService;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EmailController.class)
@ActiveProfiles("test")
@Import({SecurityConfig.class, TestConfig.class})
class EmailControllerTest {

  private static final String API_KEY_HEADER = "X-API-KEY";
  private static final String API_KEY_VALUE = "test-api-key";

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private EmailService emailService;

  @MockBean private EmailTemplateService templateService;

  private EmailRequest emailRequest;
  private TemplateEmailRequest templateEmailRequest;
  private TemplateEmailRequest invalidTemplateEmailRequest;
  private EmailResponse emailResponse;

  @BeforeEach
  void setUp() {

    Map<String, String> templateParams = Map.of(
        "mentorName", "Mike",
        "program", "Ad-hoc mentorship",
        "menteeName", "Alice",
        "deadline", "27/09/25",
        "teamEmailSignature", "Best Regards"
    );


    emailRequest =
        EmailRequest.builder()
            .to("recipient@example.com")
            .subject("Test Subject")
            .body("Test Body")
            .html(false)
            .build();

    templateEmailRequest =
        TemplateEmailRequest.builder()
            .to("recipient@example.com")
            .templateType(FEEDBACK_MENTOR_ADHOC)
            .templateParameters(templateParams)
            .html(false)
            .build();

    invalidTemplateEmailRequest =
        TemplateEmailRequest.builder()
            .templateType(FEEDBACK_MENTOR_ADHOC)
            .templateParameters(templateParams)
            .html(false)
            .build();

    emailResponse =
        EmailResponse.builder()
            .success(true)
            .message("Email sent successfully")
            .timestamp(OffsetDateTime.now())
            .recipient("recipient@example.com")
            .build();
  }

  @Test
  @DisplayName(
      "Given valid email request, when sending email via API, then should return success response")
  void shouldSendEmailSuccessfully() throws Exception {
    when(emailService.sendEmail(any(EmailRequest.class))).thenReturn(emailResponse);

    mockMvc
        .perform(
            post("/api/platform/v1/email/send")
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value("Email sent successfully"))
        .andExpect(jsonPath("$.recipient").value("recipient@example.com"));
  }

  @Test
  @DisplayName(
      "Given invalid email request without recipient, when sending email, then should return bad request")
  void shouldReturnBadRequestForInvalidEmail() throws Exception {
    EmailRequest invalidRequest =
        EmailRequest.builder().to("").subject("Test Subject").body("Test Body").build();

    mockMvc
        .perform(
            post("/api/platform/v1/email/send")
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Given invalid email format, when sending email, then should return bad request")
  void shouldReturnBadRequestForInvalidEmailFormat() throws Exception {
    EmailRequest invalidRequest =
        EmailRequest.builder()
            .to("invalid-email")
            .subject("Test Subject")
            .body("Test Body")
            .build();

    mockMvc
        .perform(
            post("/api/platform/v1/email/send")
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName(
      "Given email service failure, when sending email, then should return internal server error")
  void shouldReturnInternalServerErrorWhenServiceFails() throws Exception {
    when(emailService.sendEmail(any(EmailRequest.class)))
        .thenThrow(new EmailSendException("Failed to send email"));

    mockMvc
        .perform(
            post("/api/platform/v1/email/send")
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailRequest)))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @DisplayName(
      "Given multiple email requests, when sending bulk emails, then should return list of responses")
  void shouldSendBulkEmails() throws Exception {
    EmailRequest request1 =
        EmailRequest.builder()
            .to("recipient1@example.com")
            .subject("Subject 1")
            .body("Body 1")
            .build();

    EmailRequest request2 =
        EmailRequest.builder()
            .to("recipient2@example.com")
            .subject("Subject 2")
            .body("Body 2")
            .build();

    EmailResponse response1 =
        EmailResponse.builder()
            .success(true)
            .message("Email sent successfully")
            .timestamp(OffsetDateTime.now())
            .recipient("recipient1@example.com")
            .build();

    EmailResponse response2 =
        EmailResponse.builder()
            .success(true)
            .message("Email sent successfully")
            .timestamp(OffsetDateTime.now())
            .recipient("recipient2@example.com")
            .build();

    when(emailService.sendBulkEmails(anyList())).thenReturn(List.of(response1, response2));

    mockMvc
        .perform(
            post("/api/platform/v1/email/send/bulk")
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(request1, request2))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].success").value(true))
        .andExpect(jsonPath("$[0].recipient").value("recipient1@example.com"))
        .andExpect(jsonPath("$[1].success").value(true))
        .andExpect(jsonPath("$[1].recipient").value("recipient2@example.com"));
  }

  @Test
  @DisplayName(
      "Given email request with HTML content, when sending email, then should process successfully")
  void shouldSendHtmlEmail() throws Exception {
    EmailRequest htmlRequest =
        EmailRequest.builder()
            .to("recipient@example.com")
            .subject("Test Subject")
            .body("<h1>HTML Content</h1>")
            .html(true)
            .build();

    when(emailService.sendEmail(any(EmailRequest.class))).thenReturn(emailResponse);

    mockMvc
        .perform(
            post("/api/platform/v1/email/send")
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(htmlRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));
  }

  @Test
  @DisplayName(
      "Given email request with CC and BCC, when sending email, then should process successfully")
  void shouldSendEmailWithCcAndBcc() throws Exception {
    EmailRequest ccBccRequest =
        EmailRequest.builder()
            .to("recipient@example.com")
            .subject("Test Subject")
            .body("Test Body")
            .cc(List.of("cc@example.com"))
            .bcc(List.of("bcc@example.com"))
            .build();

    when(emailService.sendEmail(any(EmailRequest.class))).thenReturn(emailResponse);

    mockMvc
        .perform(
            post("/api/platform/v1/email/send")
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ccBccRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));
  }

  @Test
  @DisplayName(
      "Given valid template email request, when sending template email via API, then should return success response")
  void shouldSendTemplateEmailSuccessfully() throws Exception {
    when(emailService.sendTemplateEmail(any(TemplateEmailRequest.class))).thenReturn(emailResponse);

    mockMvc
        .perform(
            post("/api/platform/v1/email/template/send")
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(templateEmailRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value("Email sent successfully"))
        .andExpect(jsonPath("$.recipient").value("recipient@example.com"));
  }

  @Test
  @DisplayName(
      "Given invalid template email request without recipient, when sending template email, then should return bad request")
  void shouldReturnBadRequestForInvalidTemplateEmail() throws Exception {
    mockMvc
        .perform(
            post("/api/platform/v1/email/template/send")
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidTemplateEmailRequest)))
        .andExpect(status().isBadRequest());
  }


}
