package com.wcc.platform.service;

import static com.wcc.platform.factories.SetupMentorFactories.createMemberProfilePictureTest;
import static com.wcc.platform.factories.SetupMentorFactories.createResourceTest;
import static com.wcc.platform.service.MentorshipService.CYCLE_CLOSED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import com.wcc.platform.domain.cms.attributes.ImageType;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycle;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import com.wcc.platform.repository.MemberProfilePictureRepository;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.MentorRepository;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceRetrievalTest {

  @Mock private MentorRepository mentorRepository;
  @Mock private MemberRepository memberRepository;
  @Mock private MemberProfilePictureRepository profilePicRepo;
  private Integer daysOpen = 10;
  private MentorshipService service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    service =
        spy(new MentorshipService(mentorRepository, memberRepository, profilePicRepo, daysOpen));
  }

  @Test
  void whenGetAllMentorsGivenCycleClosedThenReturnDtosWithoutCycle() {
    var mentor =
        mock(
            com.wcc.platform.domain.platform.mentorship.Mentor.class,
            withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
    var dto = mock(com.wcc.platform.domain.platform.mentorship.MentorDto.class);
    when(mentor.toDto()).thenReturn(dto);
    when(mentorRepository.getAll()).thenReturn(List.of(mentor));

    doReturn(CYCLE_CLOSED).when(service).getCurrentCycle();

    var result = service.getAllMentors();

    assertEquals(List.of(dto), result);
    verify(mentor, times(1)).toDto();
    verify(mentor, never()).toDto(any(MentorshipCycle.class));
  }

  @Test
  void whenGetAllMentorsGivenAdHocCycleOpenThenReturnDtosWithCycle() {
    var mentor =
        mock(
            com.wcc.platform.domain.platform.mentorship.Mentor.class,
            withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
    var dto = mock(com.wcc.platform.domain.platform.mentorship.MentorDto.class);
    when(mentor.toDto(any(MentorshipCycle.class))).thenReturn(dto);
    when(mentorRepository.getAll()).thenReturn(List.of(mentor));

    var cycle = new MentorshipCycle(MentorshipType.AD_HOC);
    doReturn(cycle).when(service).getCurrentCycle();

    var result = service.getAllMentors();

    assertEquals(List.of(dto), result);
    verify(mentor, times(1)).toDto(cycle);
    verify(mentor, never()).toDto();
  }

  @Test
  void whenGetAllMentorsGivenLongTermCycleOpenThenReturnDtosWithCycle() {
    var mentor =
        mock(
            com.wcc.platform.domain.platform.mentorship.Mentor.class,
            withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
    var dto = mock(com.wcc.platform.domain.platform.mentorship.MentorDto.class);
    when(mentor.toDto(any(MentorshipCycle.class))).thenReturn(dto);
    when(mentorRepository.getAll()).thenReturn(List.of(mentor));

    var cycle = new MentorshipCycle(MentorshipType.LONG_TERM);
    doReturn(cycle).when(service).getCurrentCycle();

    var result = service.getAllMentors();

    assertEquals(List.of(dto), result);
    verify(mentor, times(1)).toDto(cycle);
    verify(mentor, never()).toDto();
  }

  @Test
  void testGetCurrentCycleReturnsLongTermDuringMarchWithinOpenDays() {
    var march3 = ZonedDateTime.of(2025, 3, 3, 10, 0, 0, 0, ZoneId.of("Europe/London"));
    doReturn(march3).when(service).nowLondon();

    var cycle = service.getCurrentCycle();

    assertEquals(new MentorshipCycle(MentorshipType.LONG_TERM, Month.MARCH), cycle);
  }

  @Test
  void testGetCurrentCycleReturnsAdHocFromMayWithinOpenDays() {
    daysOpen = 7;
    service =
        spy(new MentorshipService(mentorRepository, memberRepository, profilePicRepo, daysOpen));
    var may2 = ZonedDateTime.of(2025, 5, 2, 9, 0, 0, 0, ZoneId.of("Europe/London"));
    doReturn(may2).when(service).nowLondon();

    var cycle = service.getCurrentCycle();

    assertEquals(new MentorshipCycle(MentorshipType.AD_HOC, Month.MAY), cycle);
  }

  @Test
  void testGetCurrentCycleReturnsClosedOutsideWindows() {
    daysOpen = 5;
    service =
        spy(new MentorshipService(mentorRepository, memberRepository, profilePicRepo, daysOpen));

    // April -> closed
    var april10 = ZonedDateTime.of(2025, 4, 10, 12, 0, 0, 0, ZoneId.of("Europe/London"));
    doReturn(april10).when(service).nowLondon();
    assertEquals(CYCLE_CLOSED, service.getCurrentCycle());

    // December -> closed
    var dec1 = ZonedDateTime.of(2025, 12, 1, 12, 0, 0, 0, ZoneId.of("Europe/London"));
    doReturn(dec1).when(service).nowLondon();
    assertEquals(CYCLE_CLOSED, service.getCurrentCycle());

    // May but beyond open days -> closed
    var may20 = ZonedDateTime.of(2025, 5, 20, 12, 0, 0, 0, ZoneId.of("Europe/London"));
    doReturn(may20).when(service).nowLondon();
    assertEquals(CYCLE_CLOSED, service.getCurrentCycle());
  }

  @Test
  @DisplayName(
      "Given mentor with profile picture, when getAllMentors is called, then images list should"
          + " contain profile picture")
  void shouldMergeProfilePictureIntoImagesWhenMentorHasProfilePicture() {
    var mentor =
        mock(
            com.wcc.platform.domain.platform.mentorship.Mentor.class,
            withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
    var dto = mock(com.wcc.platform.domain.platform.mentorship.MentorDto.class);
    when(mentor.toDto()).thenReturn(dto);
    when(dto.getId()).thenReturn(1L);
    when(mentorRepository.getAll()).thenReturn(List.of(mentor));

    var resource = createResourceTest();
    var profilePicture = createMemberProfilePictureTest(1L).toBuilder().resource(resource).build();
    when(profilePicRepo.findByMemberId(1L)).thenReturn(Optional.of(profilePicture));

    doReturn(CYCLE_CLOSED).when(service).getCurrentCycle();

    var result = service.getAllMentors();

    assertThat(result).hasSize(1);
    var mentorDtoResult = result.getFirst();
    assertThat(mentorDtoResult.getImages()).hasSize(1);
    assertThat(mentorDtoResult.getImages().getFirst().path())
        .isEqualTo(resource.getDriveFileLink());
    assertThat(mentorDtoResult.getImages().getFirst().type()).isEqualTo(ImageType.DESKTOP);
  }

  @Test
  @DisplayName(
      "Given mentor without profile picture, when getAllMentors is called, then images list"
          + " should be empty")
  void shouldReturnEmptyImagesWhenMentorHasNoProfilePicture() {
    var mentor =
        mock(
            com.wcc.platform.domain.platform.mentorship.Mentor.class,
            withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
    var dto = mock(com.wcc.platform.domain.platform.mentorship.MentorDto.class);
    when(mentor.toDto()).thenReturn(dto);
    when(dto.getId()).thenReturn(1L);
    when(mentorRepository.getAll()).thenReturn(List.of(mentor));
    when(profilePicRepo.findByMemberId(1L)).thenReturn(Optional.empty());

    doReturn(CYCLE_CLOSED).when(service).getCurrentCycle();

    var result = service.getAllMentors();

    assertThat(result).hasSize(1);
    var mentorDtoResult = result.getFirst();
    assertThat(mentorDtoResult.getImages()).isNullOrEmpty();
  }

  @Test
  @DisplayName(
      "Given profile picture fetch throws exception, when getAllMentors is called, then images"
          + " should be empty and exception should be logged")
  void shouldHandleExceptionWhenFetchingProfilePictureFails() {
    var mentor =
        mock(
            com.wcc.platform.domain.platform.mentorship.Mentor.class,
            withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
    var dto = mock(com.wcc.platform.domain.platform.mentorship.MentorDto.class);
    when(mentor.toDto()).thenReturn(dto);
    when(dto.getId()).thenReturn(1L);
    when(mentorRepository.getAll()).thenReturn(List.of(mentor));
    when(profilePicRepo.findByMemberId(1L)).thenThrow(new RuntimeException("Database error"));

    doReturn(CYCLE_CLOSED).when(service).getCurrentCycle();

    var result = service.getAllMentors();

    assertThat(result).hasSize(1);
    var mentorDtoResult = result.getFirst();
    assertThat(mentorDtoResult.getImages()).isNullOrEmpty();
  }
}
