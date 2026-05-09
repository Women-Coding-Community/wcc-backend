package com.wcc.platform.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wcc.platform.domain.auth.UserAccount;
import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.type.RoleType;
import com.wcc.platform.domain.template.RenderedTemplate;
import com.wcc.platform.domain.template.TemplateRequest;
import com.wcc.platform.domain.template.TemplateType;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.UserAccountRepository;
import com.wcc.platform.repository.postgres.DefaultDatabaseSetup;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EmailTemplateControllerTest extends DefaultDatabaseSetup {
  private static final String API_TEMPLATE_PREVIEW = "/api/platform/v1/email/template/preview";
  private static final String API_LOGIN = "/api/auth/login";
  private static final String ADMIN_EMAIL = "admin@wcc.com";
  private static final String ADMIN_PASSWORD = "password";

  @LocalServerPort private int port;

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private MemberRepository memberRepository;
  @Autowired private UserAccountRepository userAccountRepository;
  @Autowired private PasswordEncoder passwordEncoder;

  private String adminToken;

  @BeforeAll
  void setUpOnce() {
    userAccountRepository.findAll().forEach(user -> userAccountRepository.deleteById(user.getId()));
    memberRepository
        .findByEmail(ADMIN_EMAIL)
        .ifPresent(member -> memberRepository.deleteById(member.getId()));

    var member = memberRepository.create(SetupMembers.createAdminMember());
    userAccountRepository.create(
        new UserAccount(
            1,
            member.getId(),
            ADMIN_EMAIL,
            passwordEncoder.encode(ADMIN_PASSWORD),
            List.of(RoleType.ADMIN),
            true));

    this.adminToken = loginAsAdmin();
  }

  @AfterAll
  void tearDownOnce() {
    userAccountRepository.findAll().forEach(user -> userAccountRepository.deleteById(user.getId()));
    memberRepository
        .findByEmail(ADMIN_EMAIL)
        .ifPresent(member -> memberRepository.deleteById(member.getId()));
  }

  @Test
  void previewValidRequestReturnsRenderedTemplate() {
    Map<String, Object> params =
        Map.of(
            "mentorName", "Alice",
            "menteeName", "Bob",
            "program", "Mentorship",
            "deadline", "2025-12-01",
            "teamEmailSignature", "Best regards");
    var request = new TemplateRequest(TemplateType.FEEDBACK_MENTOR_ADHOC, params);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(adminToken);
    headers.add("X-API-KEY", "test-api-key");

    HttpEntity<TemplateRequest> entity = new HttpEntity<>(request, headers);

    ResponseEntity<RenderedTemplate> response =
        restTemplate.postForEntity(
            "http://localhost:" + port + API_TEMPLATE_PREVIEW, entity, RenderedTemplate.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().subject()).contains("Alice");
    assertThat(response.getBody().body()).contains("Bob");
  }

  @Test
  void previewMissingParamsReturnsBadRequest() {
    var request =
        new TemplateRequest(TemplateType.FEEDBACK_MENTOR_ADHOC, Map.of("mentorName", "Alice"));

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(adminToken);
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

  private String loginAsAdmin() {
    var loginRequest = new LoginRequest(ADMIN_EMAIL, ADMIN_PASSWORD);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    ResponseEntity<LoginResponse> response =
        restTemplate.postForEntity(
            "http://localhost:" + port + API_LOGIN,
            new HttpEntity<>(loginRequest, headers),
            LoginResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().token()).isNotBlank();
    return response.getBody().token();
  }

  private record LoginRequest(String email, String password) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  private record LoginResponse(String token) {}

  private static final class SetupMembers {
    private static Member createAdminMember() {
      return Member.builder()
          .fullName("Admin User")
          .position("Administrator")
          .email(ADMIN_EMAIL)
          .slackDisplayName("admin")
          .country(new Country("US", "United States"))
          .build();
    }
  }
}
