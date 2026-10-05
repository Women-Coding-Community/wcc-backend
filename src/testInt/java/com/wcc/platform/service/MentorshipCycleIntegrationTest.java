package com.wcc.platform.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.auth.UserAccount;
import com.wcc.platform.domain.platform.mentorship.CycleStatus;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleEntity;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.type.RoleType;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.MentorshipCycleRepository;
import com.wcc.platform.repository.UserAccountRepository;
import com.wcc.platform.repository.postgres.DefaultDatabaseSetup;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * Integration tests for MentorshipCycleRepository with PostgreSQL. Tests cycle queries and
 * management operations.
 */
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MentorshipCycleIntegrationTest extends DefaultDatabaseSetup {

  private static final int TEST_YEAR = 2099;
  private static final String ADMIN_EMAIL = "mentorship-cycle-admin@wcc.com";
  private static final String ADMIN_PASSWORD = "password";
  private static final String CYCLES_PATH = "/api/platform/v1/admin/mentorship/cycles";
  private static final String LOGIN_PATH = "/api/auth/login";

  @LocalServerPort private int port;

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private MentorshipCycleRepository cycleRepository;
  @Autowired private MemberRepository memberRepository;
  @Autowired private UserAccountRepository userAccountRepository;
  @Autowired private PasswordEncoder passwordEncoder;

  private String adminToken;

  @BeforeAll
  void setUpAdmin() {
    final Member member =
        memberRepository
            .findByEmail(ADMIN_EMAIL)
            .orElseGet(
                () ->
                    memberRepository.create(
                        Member.builder()
                            .fullName("Mentorship Cycle Admin")
                            .position("Administrator")
                            .email(ADMIN_EMAIL)
                            .slackDisplayName("mentorship-cycle-admin")
                            .build()));
    userAccountRepository
        .findByEmail(ADMIN_EMAIL)
        .orElseGet(
            () ->
                userAccountRepository.create(
                    new UserAccount(
                        null,
                        member.getId(),
                        ADMIN_EMAIL,
                        passwordEncoder.encode(ADMIN_PASSWORD),
                        List.of(RoleType.ADMIN),
                        true)));
    adminToken = loginAsAdmin();
  }

  @BeforeEach
  void setUp() {
    cycleRepository
        .findByYearAndTypeAndMonth(
            Year.of(TEST_YEAR), MentorshipType.AD_HOC, Month.DECEMBER)
        .ifPresent(cycle -> cycleRepository.deleteById(cycle.getCycleId()));

    // Clean up before starting
    cycleRepository
        .findByYearAndType(Year.of(2026), MentorshipType.LONG_TERM)
        .ifPresent(c -> cycleRepository.deleteById(c.getCycleId()));

    // Setup cycle
    cycleRepository.create(
        MentorshipCycleEntity.builder()
            .cycleYear(Year.of(2026))
            .mentorshipType(MentorshipType.LONG_TERM)
            .cycleMonth(Month.JANUARY)
            .registrationStartDate(LocalDate.now().minusDays(1))
            .registrationEndDate(LocalDate.now().plusDays(10))
            .cycleStartDate(LocalDate.now().plusDays(15))
            .status(CycleStatus.OPEN)
            .maxMenteesPerMentor(3)
            .description("Test Cycle")
            .build());
  }

  @AfterAll
  void tearDownAdmin() {
    userAccountRepository
        .findByEmail(ADMIN_EMAIL)
        .ifPresent(user -> userAccountRepository.deleteById(user.getId()));
    memberRepository
        .findByEmail(ADMIN_EMAIL)
        .ifPresent(member -> memberRepository.deleteById(member.getId()));
  }

  @Test
  @DisplayName(
      "Given database is seeded with cycles, when finding open cycle, then it should return the open cycle")
  void shouldFindOpenCycle() {
    final Optional<MentorshipCycleEntity> openCycle = cycleRepository.findOpenCycle();

    assertThat(openCycle).isPresent();
    assertThat(openCycle.get().getStatus()).isEqualTo(CycleStatus.OPEN);
  }

  @Test
  @DisplayName(
      "Given database is seeded, when finding all cycles, then it should return all cycles")
  void shouldFindAllCycles() {
    final List<MentorshipCycleEntity> allCycles = cycleRepository.getAll();

    assertThat(allCycles).isNotEmpty();
    // V18 migration seeds 8 cycles for 2026
    assertThat(allCycles.size()).isGreaterThanOrEqualTo(8);
  }

  @Test
  @DisplayName(
      "Given database is seeded, when finding cycles by status OPEN, then it should return open cycles")
  void shouldFindCyclesByStatusOpen() {
    final List<MentorshipCycleEntity> openCycles = cycleRepository.findByStatus(CycleStatus.OPEN);

    assertThat(openCycles).isNotEmpty();
    assertThat(openCycles).allMatch(cycle -> cycle.getStatus() == CycleStatus.OPEN);
  }

  @Test
  @DisplayName(
      "Given database is seeded, when finding cycles by status DRAFT, then it should return draft cycles")
  void shouldFindCyclesByStatusDraft() {
    final List<MentorshipCycleEntity> draftCycles = cycleRepository.findByStatus(CycleStatus.DRAFT);

    assertThat(draftCycles).isNotEmpty();
    assertThat(draftCycles).allMatch(cycle -> cycle.getStatus() == CycleStatus.DRAFT);
  }

  @Test
  @DisplayName(
      "Given database is seeded, when finding cycle by ID, then it should return the correct cycle")
  void shouldFindCycleById() {
    // First get all cycles to find a valid ID
    final List<MentorshipCycleEntity> allCycles = cycleRepository.getAll();
    assertThat(allCycles).isNotEmpty();

    final Long validCycleId = allCycles.getFirst().getCycleId();
    final Optional<MentorshipCycleEntity> found = cycleRepository.findById(validCycleId);

    assertThat(found).isPresent();
    assertThat(found.get().getCycleId()).isEqualTo(validCycleId);
  }

  @Test
  @DisplayName("Given non-existent cycle ID, when finding by ID, then it should return empty")
  void shouldReturnEmptyForNonExistentCycleId() {
    final Optional<MentorshipCycleEntity> found = cycleRepository.findById(99L);

    assertThat(found).isEmpty();
  }

  @Test
  @DisplayName(
      "Given seeded cycles, when checking cycle properties, then they should have valid data")
  void shouldHaveValidCycleData() {
    final List<MentorshipCycleEntity> allCycles = cycleRepository.getAll();
    assertThat(allCycles).isNotEmpty();

    final MentorshipCycleEntity cycle = allCycles.getFirst();

    assertThat(cycle.getCycleId()).isNotNull();
    assertThat(cycle.getCycleYear()).isNotNull();
    assertThat(cycle.getMentorshipType()).isNotNull();
    assertThat(cycle.getStatus()).isNotNull();
    assertThat(cycle.getRegistrationStartDate()).isNotNull();
    assertThat(cycle.getRegistrationEndDate()).isNotNull();
    assertThat(cycle.getCycleStartDate()).isNotNull();
    assertThat(cycle.getMaxMenteesPerMentor()).isGreaterThan(0);
  }

  @Test
  @DisplayName("Given valid cycle request, when posting it, then return 201 with draft cycle")
  void shouldCreateCycleThroughPostEndpoint() throws Exception {
    final ResponseEntity<String> response =
        restTemplate.exchange(
            url(),
            HttpMethod.POST,
            requestEntity(validRequest()),
            String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    final var body = objectMapper.readTree(response.getBody());
    assertThat(body.get("status").asText()).isEqualTo("DRAFT");
    assertThat(body.get("cycleYear").asInt()).isEqualTo(TEST_YEAR);
    assertThat(body.get("mentorshipType").asText()).isEqualTo("Ad-Hoc");
    assertThat(body.get("cycleMonth").asText()).isEqualTo("DECEMBER");
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
          "cycleMonth": 12,
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
    final CycleRequest validRequest = validRequest();
    final CycleRequest request =
        new CycleRequest(
            validRequest.cycleYear(),
            13,
            validRequest.mentorshipType(),
            validRequest.registrationStartDate(),
            validRequest.registrationEndDate(),
            validRequest.cycleStartDate(),
            validRequest.cycleEndDate(),
            validRequest.maxMenteesPerMentor(),
            validRequest.description());

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
            validRequest.cycleYear(),
            validRequest.cycleMonth(),
            validRequest.mentorshipType(),
            validRequest.registrationStartDate(),
            validRequest.registrationEndDate(),
            validRequest.cycleStartDate(),
            validRequest.cycleEndDate(),
            0,
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
            validRequest.cycleYear(),
            validRequest.cycleMonth(),
            validRequest.mentorshipType(),
            validRequest.registrationStartDate(),
            "2099-11-30",
            validRequest.cycleStartDate(),
            validRequest.cycleEndDate(),
            validRequest.maxMenteesPerMentor(),
            validRequest.description());

    final ResponseEntity<String> response =
        restTemplate.exchange(
            url(), HttpMethod.POST, requestEntity(request), String.class);

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
        TEST_YEAR,
        12,
        "AD_HOC",
        "2099-12-01",
        "2099-12-15",
        "2099-12-20",
        "2100-02-20",
        5,
        "December ad-hoc cycle");
  }

  private String loginAsAdmin() {
    final HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    final ResponseEntity<LoginResponse> response =
        restTemplate.postForEntity(
            "http://localhost:" + port + LOGIN_PATH,
            new HttpEntity<>(new LoginRequest(ADMIN_EMAIL, ADMIN_PASSWORD), headers),
            LoginResponse.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    return response.getBody().token();
  }

  private record LoginRequest(String email, String password) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  private record LoginResponse(String token) {}

  private record CycleRequest(
      int cycleYear,
      int cycleMonth,
      String mentorshipType,
      String registrationStartDate,
      String registrationEndDate,
      String cycleStartDate,
      String cycleEndDate,
      int maxMenteesPerMentor,
      String description) {}
}
