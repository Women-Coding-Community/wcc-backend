package com.wcc.platform.service;

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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Shared PostgreSQL setup for mentorship cycle integration tests. */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class MentorshipCycleIntegrationTestSupport extends DefaultDatabaseSetup {

  protected static final int TEST_YEAR = 2099;
  protected static final String ADMIN_EMAIL = "mentorship-cycle-admin@wcc.com";
  protected static final String ADMIN_PASSWORD = "password";
  protected static final String CYCLES_PATH = "/api/platform/v1/admin/mentorship/cycles";
  protected static final String LOGIN_PATH = "/api/auth/login";

  @LocalServerPort protected int port;

  @Autowired protected TestRestTemplate restTemplate;
  @Autowired protected MentorshipCycleRepository cycleRepository;
  @Autowired protected MentorshipCycleService cycleService;
  @Autowired protected MemberRepository memberRepository;
  @Autowired protected UserAccountRepository userAccountRepository;
  @Autowired protected PasswordEncoder passwordEncoder;

  protected String adminToken;

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
  void setUpCycle() {
    cycleRepository
        .findByYearAndTypeAndMonth(
            Year.of(TEST_YEAR), MentorshipType.AD_HOC, Month.NOVEMBER)
        .ifPresent(cycle -> cycleRepository.deleteById(cycle.getCycleId()));
    cycleRepository
        .findByYearAndType(Year.of(2026), MentorshipType.LONG_TERM)
        .ifPresent(cycle -> cycleRepository.deleteById(cycle.getCycleId()));
    cycleRepository.create(
        MentorshipCycleEntity.builder()
            .cycleYear(Year.of(2026))
            .mentorshipType(MentorshipType.LONG_TERM)
            .cycleMonth(Month.FEBRUARY)
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

  protected String loginAsAdmin() {
    final HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    final ResponseEntity<LoginResponse> response =
        restTemplate.postForEntity(
            "http://localhost:" + port + LOGIN_PATH,
            new HttpEntity<>(new LoginRequest(ADMIN_EMAIL, ADMIN_PASSWORD), headers),
            LoginResponse.class);
    return response.getBody().token();
  }

  private record LoginRequest(String email, String password) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  private record LoginResponse(String token) {}
}
