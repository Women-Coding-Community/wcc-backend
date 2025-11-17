package com.wcc.platform.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.wcc.platform.domain.template.RenderedTemplate;
import com.wcc.platform.domain.template.TemplateRequest;
import com.wcc.platform.domain.template.TemplateType;
import com.wcc.platform.repository.postgres.DefaultDatabaseSetup;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class EmailTemplateControllerTest extends DefaultDatabaseSetup {
  private static final String API_TEMPLATE_PREVIEW = "/api/platform/v1/email/template/preview";

  @LocalServerPort private int port;

  @Autowired private TestRestTemplate restTemplate;

  @Test
  void previewValidRequestReturnsRenderedTemplate() {
    var request = new TemplateRequest();
    request.setTemplateType(TemplateType.FEEDBACK_MENTOR_ADHOC);
    request.setParams(
        Map.of(
            "mentorName", "Alice",
            "menteeName", "Bob",
            "program", "Mentorship",
            "deadline", "2025-12-01"));

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add("X-API-KEY", "test-api-key");

    HttpEntity<TemplateRequest> entity = new HttpEntity<>(request, headers);

    ResponseEntity<RenderedTemplate> response =
        restTemplate.postForEntity(
            "http://localhost:" + port + API_TEMPLATE_PREVIEW, entity, RenderedTemplate.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    // assertThat(response.getBody().getSubject()).contains("Alice");
    assertThat(response.getBody().getBody()).contains("Bob");
  }

  @Test
  void previewMissingParamsReturnsBadRequest() {
    var request = new TemplateRequest();
    request.setTemplateType(TemplateType.FEEDBACK_MENTOR_ADHOC);
    request.setParams(Map.of("mentorName", "Alice"));

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add("X-API-KEY", "test-api-key");

    HttpEntity<TemplateRequest> entity = new HttpEntity<>(request, headers);

    ResponseEntity<String> response =
        restTemplate.exchange(
            "http://localhost:" + port + API_TEMPLATE_PREVIEW,
            HttpMethod.POST,
            entity,
            String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).contains("Missing required parameters");
  }
}
