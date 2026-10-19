package com.wcc.platform.service;

import static org.assertj.core.api.Assertions.assertThat;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Month;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

/** Integration tests for mentorship cycle REST endpoints. */
@ActiveProfiles("test")
class MentorshipCycleEndpointIntegrationTest extends MentorshipCycleIntegrationTestSupport {

  @Autowired private ObjectMapper objectMapper;

  @Test
  @DisplayName("Given a past year, when getting cycles, then it should throw ConstraintViolationException")
  void shouldRejectPastYear() {
    org.assertj.core.api.Assertions.assertThatThrownBy(() -> cycleService.getCycles(java.time.Year.of(2025)))
        .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
  }

  @Test
  @DisplayName("Given valid cycle request, when posting it, then return 201 with draft cycle")
  void shouldCreateCycleThroughPostEndpoint() throws Exception {
    final ResponseEntity<String> response =
        restTemplate.exchange(url(), HttpMethod.POST, requestEntity(validRequest()), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    final var body = objectMapper.readTree(response.getBody());
    assertThat(body.get("status").asText()).isEqualTo("DRAFT");
    assertThat(body.get("cycleYear").asInt()).isEqualTo(TEST_YEAR);
    assertThat(body.get("mentorshipType").asText()).isEqualTo("Ad-Hoc");
    assertThat(body.get("cycleMonth").asText()).isEqualTo("NOVEMBER");
  }

  @Test
  @DisplayName("Given existing cycle, when posting duplicate, then return 409")
  void shouldReturnConflictForDuplicateCycle() {
    final HttpEntity<CycleRequest> entity = requestEntity(validRequest());
    restTemplate.exchange(url(), HttpMethod.POST, entity, String.class);

    final ResponseEntity<String> response =
        restTemplate.exchange(url(), HttpMethod.POST, entity, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(response.getBody()).contains("already exists");
  }

  @Test
  @DisplayName("Given missing required field, when posting cycle, then return 400")
  void shouldReturnBadRequestForMissingRequiredField() {
    final String body =
        """
        {
          "cycleMonth": "DECEMBER",
          "mentorshipType": "AD_HOC",
          "registrationStartDate": "2099-12-01",
          "registrationEndDate": "2099-12-15",
          "cycleStartDate": "2099-12-20"
        }
        """;

    final ResponseEntity<String> response =
        restTemplate.exchange(url(), HttpMethod.POST, requestEntity(body), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).contains("cycleYear");
  }

  @Test
  @DisplayName("Given invalid cycle month, when posting cycle, then return 400")
  void shouldReturnBadRequestForInvalidCycleMonth() {
    final CycleRequest request =
        new CycleRequest(
            TEST_YEAR, Month.JANUARY, "AD_HOC", "2099-11-01", "2099-11-15",
            "2099-11-20", "2100-02-20", 5, "January ad-hoc cycle");

    final ResponseEntity<String> response =
        restTemplate.exchange(url(), HttpMethod.POST, requestEntity(request), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).contains("cycleMonth");
  }

  @Test
  @DisplayName("Given invalid mentee cap, when posting cycle, then return 400")
  void shouldReturnBadRequestForInvalidMaxMenteesPerMentor() {
    final CycleRequest validRequest = validRequest();
    final CycleRequest request =
        new CycleRequest(
            validRequest.cycleYear(), validRequest.cycleMonth(), validRequest.mentorshipType(),
            validRequest.registrationStartDate(), validRequest.registrationEndDate(),
            validRequest.cycleStartDate(), validRequest.cycleEndDate(), 0,
            validRequest.description());

    final ResponseEntity<String> response =
        restTemplate.exchange(url(), HttpMethod.POST, requestEntity(request), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).contains("maxMenteesPerMentor");
  }

  @Test
  @DisplayName("Given invalid date order, when posting cycle, then return 400")
  void shouldReturnBadRequestForInvalidDateOrder() {
    final CycleRequest validRequest = validRequest();
    final CycleRequest request =
        new CycleRequest(
            validRequest.cycleYear(), validRequest.cycleMonth(), validRequest.mentorshipType(),
            validRequest.registrationStartDate(), "2099-10-30", validRequest.cycleStartDate(),
            validRequest.cycleEndDate(), validRequest.maxMenteesPerMentor(),
            validRequest.description());

    final ResponseEntity<String> response =
        restTemplate.exchange(url(), HttpMethod.POST, requestEntity(request), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).contains("Registration end date");
  }

  private String url() {
    return "http://localhost:" + port + CYCLES_PATH;
  }

  private HttpEntity<CycleRequest> requestEntity(final CycleRequest request) {
    return new HttpEntity<>(request, headers());
  }

  private HttpEntity<String> requestEntity(final String body) {
    return new HttpEntity<>(body, headers());
  }

  private HttpHeaders headers() {
    final HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(adminToken);
    headers.add("X-API-KEY", "test-api-key");
    return headers;
  }

  private CycleRequest validRequest() {
    return new CycleRequest(
        TEST_YEAR, Month.NOVEMBER, "AD_HOC", "2099-11-01", "2099-11-15",
        "2099-11-20", "2100-02-20", 5, "November ad-hoc cycle");
  }

  private record CycleRequest(
      int cycleYear,
      Month cycleMonth,
      String mentorshipType,
      String registrationStartDate,
      String registrationEndDate,
      String cycleStartDate,
      String cycleEndDate,
      int maxMenteesPerMentor,
      String description) {}
}
