package com.wcc.platform.service;

import static com.wcc.platform.factories.SetupMentorFactories.createMemberProfilePictureTest;
import static com.wcc.platform.factories.SetupMentorFactories.createResourceTest;
import static com.wcc.platform.service.MentorshipService.CYCLE_CLOSED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import com.wcc.platform.domain.cms.attributes.ImageType;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.domain.platform.mentorship.MentorDto;
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
  @Mock private MentorshipNotificationService notificationService;
  private MentorshipService service;

  @BeforeEach
  void setUp() {
    final var daysOpen = 10;

    MockitoAnnotations.openMocks(this);
    service =
        spy(
            new MentorshipService(
                mentorRepository, memberRepository, profilePicRepo, daysOpen, notificationService));
  }

  @Test
  void whenGetAllMentorsGivenCycleClosedThenReturnDtos() {
    var mentor = mock(Mentor.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
    var dto = mock(MentorDto.class);
    when(mentor.toDto()).thenReturn(dto);
    when(mentorRepository.getAll()).thenReturn(List.of(mentor));
    when(profilePicRepo.findByMemberId(1L)).thenReturn(Optional.empty());

    doReturn(CYCLE_CLOSED).when(service).getCurrentCycle();

    var result = service.getAllMentors();

    assertThat(result).hasSize(1);
  }

  @Test
  void whenGetAllMentorsGivenAdHocCycleOpenThenReturnDtos() {
    var mentor = mock(Mentor.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
    var dto = mock(MentorDto.class);
    when(mentor.toDto(any())).thenReturn(dto);
    when(dto.getId()).thenReturn(1L);
    when(mentorRepository.getAll()).thenReturn(List.of(mentor));

    var cycle = new MentorshipCycle(MentorshipType.AD_HOC, Month.MAY);
    doReturn(cycle).when(service).getCurrentCycle();

    var result = service.getAllMentors();

    assertThat(result).hasSize(1);
  }

  @Test
  void whenGetAllMentorsGivenLongTermCycleOpenThenReturnDtos() {
    var mentor = mock(Mentor.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
    var dto = mock(MentorDto.class);
    when(mentor.toDto(any())).thenReturn(dto);
    when(dto.getId()).thenReturn(1L);
    when(mentorRepository.getAll()).thenReturn(List.of(mentor));
    when(profilePicRepo.findByMemberId(1L)).thenReturn(Optional.empty());

    var cycle = new MentorshipCycle(MentorshipType.LONG_TERM, Month.MARCH);
    doReturn(cycle).when(service).getCurrentCycle();

    var result = service.getAllMentors();

    assertThat(result).hasSize(1);
  }

  private <T> T any() {
    return org.mockito.ArgumentMatchers.any();
  }

  @Test
  void testGetCurrentCycleReturnsLongTermDuringMarchWithinOpenDays() {
    var marchDate = ZonedDateTime.of(2024, 3, 5, 12, 0, 0, 0, ZoneId.of("Europe/London"));
    doReturn(marchDate).when(service).nowLondon();

    var result = service.getCurrentCycle();

    assertEquals(MentorshipType.LONG_TERM, result.cycle());
  }

  @Test
  void testGetCurrentCycleReturnsAdHocFromMayWithinOpenDays() {
    var mayDate = ZonedDateTime.of(2024, 5, 5, 12, 0, 0, 0, ZoneId.of("Europe/London"));
    doReturn(mayDate).when(service).nowLondon();

    var result = service.getCurrentCycle();

    assertEquals(MentorshipType.AD_HOC, result.cycle());
    assertEquals(Month.MAY, result.month());
  }

  @Test
  void testGetCurrentCycleReturnsClosedOutsideWindows() {
    // January - Closed
    var janDate = ZonedDateTime.of(2024, 1, 5, 12, 0, 0, 0, ZoneId.of("Europe/London"));
    doReturn(janDate).when(service).nowLondon();
    assertEquals(CYCLE_CLOSED, service.getCurrentCycle());

    // March after 10 days - Closed
    var marchLateDate = ZonedDateTime.of(2024, 3, 15, 12, 0, 0, 0, ZoneId.of("Europe/London"));
    doReturn(marchLateDate).when(service).nowLondon();
    assertEquals(CYCLE_CLOSED, service.getCurrentCycle());

    // April - Closed
    var aprilDate = ZonedDateTime.of(2024, 4, 5, 12, 0, 0, 0, ZoneId.of("Europe/London"));
    doReturn(aprilDate).when(service).nowLondon();
    assertEquals(CYCLE_CLOSED, service.getCurrentCycle());
  }

  @Test
  @DisplayName(
      "Given mentor with profile picture, when getAllMentors is called, then images list should"
          + " contain profile picture")
  void shouldMergeProfilePictureIntoImagesWhenMentorHasProfilePicture() {
    var mentor = mock(Mentor.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
    var dto = mock(MentorDto.class);
    when(mentor.toDto()).thenReturn(dto);
    when(dto.getId()).thenReturn(1L);
    when(mentorRepository.getAll()).thenReturn(List.of(mentor));
    when(profilePicRepo.findByMemberId(1L)).thenReturn(Optional.empty());

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
    var mentor = mock(Mentor.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
    var dto = mock(MentorDto.class);
    when(mentor.toDto()).thenReturn(dto);
    when(dto.getId()).thenReturn(1L);
    when(mentorRepository.getAll()).thenReturn(List.of(mentor));
    when(profilePicRepo.findByMemberId(1L)).thenReturn(Optional.empty());

    doReturn(CYCLE_CLOSED).when(service).getCurrentCycle();

    var result = service.getAllMentors();

    assertThat(result).hasSize(1);
    var mentorDto = result.getFirst();
    assertThat(mentorDto.getImages()).isNullOrEmpty();
  }

  @Test
  @DisplayName(
      "Given profile picture fetch throws exception, when getAllMentors is called, then images"
          + " should be empty and exception should be logged")
  void shouldHandleExceptionWhenFetchingProfilePictureFails() {
    var mentor = mock(Mentor.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
    var dto = mock(MentorDto.class);
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
