package com.wcc.platform.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.configuration.security.RequiresRole;
import com.wcc.platform.domain.platform.mentorship.CycleStatus;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleCreateRequest;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleEntity;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import com.wcc.platform.domain.platform.type.RoleType;
import com.wcc.platform.repository.MentorshipCycleRepository;
import com.wcc.platform.service.MenteeWorkflowService;
import com.wcc.platform.service.MentorshipCycleService;
import com.wcc.platform.service.MentorshipMatchingService;
import com.wcc.platform.service.MentorshipRecommendationService;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MentorshipAdminMatchesControllerTest {

  @Mock private MentorshipMatchingService matchingService;
  @Mock private MentorshipCycleRepository cycleRepository;
  @Mock private MentorshipRecommendationService recommendationService;
  @Mock private MenteeWorkflowService workflowService;
  @Mock private MentorshipCycleService cycleService;

  @Test
  @DisplayName("Given valid cycle request, when creating cycle, then return created cycle")
  void shouldCreateCycleAndDelegateToService() {
    final var controller = controller();
    final var request = request();
    final var created =
        MentorshipCycleEntity.builder().cycleId(9L).status(CycleStatus.DRAFT).build();
    when(cycleService.createCycle(request)).thenReturn(created);

    final var response = controller.createCycle(request);

    assertThat(response.getStatusCode().value()).isEqualTo(201);
    assertThat(response.getBody()).isSameAs(created);
    verify(cycleService).createCycle(request);
  }

  @Test
  @DisplayName("Given create cycle endpoint, when checking authorization, then require admin roles")
  void shouldRequirePlatformOrMentorshipAdminRole() throws NoSuchMethodException {
    final var method =
        MentorshipAdminMatchesController.class.getDeclaredMethod(
            "createCycle", MentorshipCycleCreateRequest.class);
    final var annotation = method.getAnnotation(RequiresRole.class);

    assertThat(annotation).isNotNull();
    assertThat(annotation.value()).containsExactly(RoleType.ADMIN, RoleType.MENTORSHIP_ADMIN);
  }

  private MentorshipAdminMatchesController controller() {
    return new MentorshipAdminMatchesController(
        matchingService, cycleRepository, recommendationService, workflowService, cycleService);
  }

  private MentorshipCycleCreateRequest request() {
    return new MentorshipCycleCreateRequest(
        2026,
        10,
        MentorshipType.AD_HOC,
        LocalDate.of(2026, 10, 1),
        LocalDate.of(2026, 10, 15),
        LocalDate.of(2026, 10, 20),
        LocalDate.of(2026, 12, 20),
        5,
        "October cycle");
  }
}
