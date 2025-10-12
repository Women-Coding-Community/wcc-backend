package com.wcc.platform.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.wcc.platform.domain.exceptions.DuplicatedMemberException;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.domain.platform.mentorship.MentorDto;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycle;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import com.wcc.platform.repository.MentorRepository;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {

  @Mock private MentorRepository mentorRepository;
  private Integer daysOpen = 10;

  private MentorshipService service;

  public MentorshipServiceTest() {
    super();
  }

  @BeforeEach
  void setUp() {
    service = spy(new MentorshipService(mentorRepository, daysOpen));
  }

  @Test
  void whenCreateGivenMentorAlreadyExistsThenThrowDuplicatedMemberException() {
    var mentor = mock(Mentor.class);
    when(mentor.getId()).thenReturn(1L);
    when(mentorRepository.findById(1L)).thenReturn(Optional.of(mentor));

    assertThrows(DuplicatedMemberException.class, () -> service.create(mentor));
    verify(mentorRepository, never()).create(any());
  }

  @Test
  void whenCreateGivenMentorDoesNotExistThenCreateMentor() {
    var mentor = mock(Mentor.class);
    when(mentor.getId()).thenReturn(2L);
    when(mentorRepository.findById(2L)).thenReturn(Optional.empty());
    when(mentorRepository.create(mentor)).thenReturn(mentor);

    var result = service.create(mentor);

    assertEquals(mentor, result);
    verify(mentorRepository).create(mentor);
  }

  @Test
  void whenGetAllMentorsGivenCycleClosedThenReturnDtosWithoutCycle() {
    var mentor = mock(Mentor.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
    var dto = mock(MentorDto.class);
    when(mentor.toDto()).thenReturn(dto);
    when(mentorRepository.getAll()).thenReturn(List.of(mentor));

    // Cycle closed -> getCurrentCycle returns null
    doReturn(null).when(service).getCurrentCycle();

    var result = service.getAllMentors();

    assertEquals(List.of(dto), result);
    verify(mentor, times(1)).toDto();
    verify(mentor, never()).toDto(any(MentorshipCycle.class));
  }

  @Test
  void whenGetAllMentorsGivenAdHocCycleOpenThenReturnDtosWithCycle() {
    var mentor = mock(Mentor.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
    var dto = mock(MentorDto.class);
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
    var mentor = mock(Mentor.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
    var dto = mock(MentorDto.class);
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
    service = spy(new MentorshipService(mentorRepository, daysOpen));
    var may2 = ZonedDateTime.of(2025, 5, 2, 9, 0, 0, 0, ZoneId.of("Europe/London"));
    doReturn(may2).when(service).nowLondon();

    var cycle = service.getCurrentCycle();

    assertEquals(new MentorshipCycle(MentorshipType.AD_HOC, Month.MAY), cycle);
  }

  @Test
  void testGetCurrentCycleReturnsClosedOutsideWindows() {
    daysOpen = 5;
    service = spy(new MentorshipService(mentorRepository, daysOpen));

    // April -> closed
    var april10 = ZonedDateTime.of(2025, 4, 10, 12, 0, 0, 0, ZoneId.of("Europe/London"));
    doReturn(april10).when(service).nowLondon();
    assertNull(service.getCurrentCycle());

    // December -> closed
    var dec1 = ZonedDateTime.of(2025, 12, 1, 12, 0, 0, 0, ZoneId.of("Europe/London"));
    doReturn(dec1).when(service).nowLondon();
    assertNull(service.getCurrentCycle());

    // May but beyond open days -> closed
    var may20 = ZonedDateTime.of(2025, 5, 20, 12, 0, 0, 0, ZoneId.of("Europe/London"));
    doReturn(may20).when(service).nowLondon();
    assertNull(service.getCurrentCycle());
  }
}
