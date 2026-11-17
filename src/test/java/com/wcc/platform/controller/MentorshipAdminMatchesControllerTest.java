package com.wcc.platform.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static com.wcc.platform.factories.MockMvcRequestFactory.getRequest;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wcc.platform.configuration.SecurityConfig;
import com.wcc.platform.configuration.TestConfig;
import com.wcc.platform.configuration.security.RequiresRole;
import com.wcc.platform.domain.platform.mentorship.CycleStatus;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleCreateRequest;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleEntity;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import com.wcc.platform.domain.platform.type.RoleType;
import com.wcc.platform.repository.MentorshipCycleRepository;
import com.wcc.platform.service.AuthService;
import com.wcc.platform.service.MenteeWorkflowService;
import com.wcc.platform.service.MentorshipCycleService;
import com.wcc.platform.service.MentorshipMatchingService;
import com.wcc.platform.service.MentorshipRecommendationService;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/** Unit test for MentorshipAdminMatchesController. */
@ActiveProfiles("test")
@Import({SecurityConfig.class, TestConfig.class})
@WebMvcTest(MentorshipAdminMatchesController.class)
class MentorshipAdminMatchesControllerTest {

  private static final String API_ALL_CYCLES = "/api/platform/v1/admin/mentorship/cycles/all";

  @Autowired private MockMvc mockMvc;
  @MockBean private MentorshipMatchingService matchingService;
  @MockBean private MentorshipCycleRepository cycleRepository;
  @MockBean private MentorshipRecommendationService recommendationService;
  @MockBean private MenteeWorkflowService workflowService;
  @MockBean private MentorshipCycleService cycleService;
  @MockBean private AuthService authService;

  @Test
  @DisplayName("Given no year, when getting all cycles, then return 200 OK with all cycles")
  void shouldReturnAllCyclesWhenNoYear() throws Exception {
    when(cycleService.getCycles(null))
        .thenReturn(List.of(cycleInYear(1L, 2025), cycleInYear(2L, 2026)));

    mockMvc
        .perform(getRequest(API_ALL_CYCLES))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(2)));
  }

  @Test
  @DisplayName("Given year 2026, when getting all cycles, then return 200 OK with 2026 cycles")
  void shouldReturnCyclesForYear() throws Exception {
    when(cycleService.getCycles(Year.of(2026))).thenReturn(List.of(cycleInYear(2L, 2026)));

    mockMvc
        .perform(getRequest(API_ALL_CYCLES).param("year", "2026"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(1)))
        .andExpect(jsonPath("$[0].cycleId", is(2)));
  }

  @Test
  @DisplayName("Given year is not a number, when getting all cycles, then return 400 BAD_REQUEST")
  void shouldReturn400WhenYearIsNotANumber() throws Exception {
    mockMvc
        .perform(getRequest(API_ALL_CYCLES).param("year", "abc"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Given valid cycle request, when creating cycle, then return created cycle")
  void shouldCreateCycleAndDelegateToService() throws NoSuchMethodException {
    final MentorshipCycleCreateRequest request = request();
    final MentorshipCycleEntity created =
        MentorshipCycleEntity.builder().cycleId(9L).status(CycleStatus.DRAFT).build();
    when(cycleService.createCycle(request)).thenReturn(created);

    final var controller =
        new MentorshipAdminMatchesController(
            matchingService,
            cycleRepository,
            recommendationService,
            workflowService,
            cycleService);
    final var response = controller.createCycle(request);

    assertThat(response.getStatusCode().value()).isEqualTo(201);
    assertThat(response.getBody()).isSameAs(created);
    verify(cycleService).createCycle(request);

    final var method =
        MentorshipAdminMatchesController.class.getDeclaredMethod(
            "createCycle", MentorshipCycleCreateRequest.class);
    final var annotation = method.getAnnotation(RequiresRole.class);
    assertThat(annotation).isNotNull();
    assertThat(annotation.value()).containsExactly(RoleType.ADMIN, RoleType.MENTORSHIP_ADMIN);
  }

  private MentorshipCycleCreateRequest request() {
    return new MentorshipCycleCreateRequest(
        Year.of(2026),
        java.time.Month.OCTOBER,
        MentorshipType.AD_HOC,
        LocalDate.of(2026, 10, 1),
        LocalDate.of(2026, 10, 15),
        LocalDate.of(2026, 10, 20),
        LocalDate.of(2026, 11, 20),
        5,
        "October cycle");
  }

  private MentorshipCycleEntity cycleInYear(final Long cycleId, final int year) {
    return MentorshipCycleEntity.builder().cycleId(cycleId).cycleYear(Year.of(year)).build();
  }
}
