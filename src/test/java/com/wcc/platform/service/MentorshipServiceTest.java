package com.wcc.platform.service;

import static com.wcc.platform.factories.SetupMentorFactories.createMemberProfilePictureTest;
import static com.wcc.platform.factories.SetupMentorFactories.createMentorDtoTest;
import static com.wcc.platform.factories.SetupMentorFactories.createMentorTest;
import static com.wcc.platform.factories.SetupMentorFactories.createResourceTest;
import static com.wcc.platform.factories.SetupMentorFactories.createUpdatedMentorTest;
import static com.wcc.platform.service.MentorshipService.CYCLE_CLOSED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import com.wcc.platform.domain.cms.attributes.ImageType;
import com.wcc.platform.domain.exceptions.DuplicatedMemberException;
import com.wcc.platform.domain.exceptions.MemberNotFoundException;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.domain.platform.mentorship.MentorDto;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycle;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import com.wcc.platform.domain.platform.type.MemberType;
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
class MentorshipServiceTest {

  @Mock private MentorRepository mentorRepository;
  @Mock private MemberRepository memberRepository;
  @Mock private MemberProfilePictureRepository profilePicRepo;
  private Integer daysOpen = 10;
  private Mentor mentor;
  private Mentor updatedMentor;
  private MentorDto mentorDto;
  private MentorshipService service;

  public MentorshipServiceTest() {
    super();
  }

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    mentor = createMentorTest();
    mentorDto = createMentorDtoTest(1L, MemberType.DIRECTOR);
    updatedMentor = createUpdatedMentorTest(mentor, mentorDto);
    service =
        spy(new MentorshipService(mentorRepository, memberRepository, profilePicRepo, daysOpen));
  }

  @Test
  void whenCreateGivenMentorAlreadyExistsThenThrowDuplicatedMemberException() {
    var mentor = mock(Mentor.class);
    when(mentor.getId()).thenReturn(1L);
    when(mentor.getEmail()).thenReturn("test@test.com");
    when(memberRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());
    when(mentorRepository.findById(1L)).thenReturn(Optional.of(mentor));

    assertThrows(DuplicatedMemberException.class, () -> service.create(mentor));
    verify(mentorRepository, never()).create(any());
  }

  @Test
  void whenCreateGivenMentorDoesNotExistThenCreateMentor() {
    var mentor = mock(Mentor.class);
    when(mentor.getId()).thenReturn(2L);
    when(mentor.getEmail()).thenReturn("newmentor@test.com");
    when(memberRepository.findByEmail("newmentor@test.com")).thenReturn(Optional.empty());
    when(mentorRepository.findById(2L)).thenReturn(Optional.empty());
    when(mentorRepository.create(mentor)).thenReturn(mentor);

    var result = service.create(mentor);

    assertEquals(mentor, result);
    verify(memberRepository).findByEmail("newmentor@test.com");
    verify(mentorRepository).create(mentor);
  }

  @Test
  void whenGetAllMentorsGivenCycleClosedThenReturnDtosWithoutCycle() {
    var mentor = mock(Mentor.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
    var dto = mock(MentorDto.class);
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
      "Given mentor exists When updating the mentor Then should update mentor attributes and return updated mentor")
  void testUpdateMentor() {
    long mentorId = 1L;
    when(mentorRepository.findById(mentorId)).thenReturn(Optional.of(mentor));
    when(mentorRepository.update(anyLong(), any())).thenReturn(updatedMentor);

    Member result = service.updateMentor(mentorId, mentorDto);

    assertEquals(updatedMentor, result);
    verify(mentorRepository).findById(mentorId);
    verify(mentorRepository).update(anyLong(), any());
  }

  @Test
  @DisplayName(
      "Given mentor does not exist When updating the mentor Then should throw MemberNotFoundException")
  void testUpdateMentorNotFound() {
    long mentorId = 1L;

    when(mentorRepository.findById(mentorId)).thenReturn(Optional.empty());

    assertThrows(MemberNotFoundException.class, () -> service.updateMentor(mentorId, mentorDto));

    verify(mentorRepository).findById(mentorId);
    verify(mentorRepository, never()).update(anyLong(), any());
  }

  @Test
  @DisplayName(
      "Given mentor exists When updating with mismatched mentor ID Then should throw IllegalArgumentException")
  void testUpdateMentorIllegalIdMismatch() {
    long mentorId = 1L;
    MentorDto newMentorDto = createMentorDtoTest(999L, MemberType.DIRECTOR);

    assertThrows(
        IllegalArgumentException.class, () -> service.updateMentor(mentorId, newMentorDto));

    verify(mentorRepository, never()).findById(anyLong());
    verify(mentorRepository, never()).update(anyLong(), any());
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

    var resource = createResourceTest();
    var profilePicture = createMemberProfilePictureTest(1L).toBuilder().resource(resource).build();
    when(profilePicRepo.findByMemberId(1L)).thenReturn(Optional.of(profilePicture));

    doReturn(CYCLE_CLOSED).when(service).getCurrentCycle();

    var result = service.getAllMentors();

    assertThat(result).hasSize(1);
    var mentorDto = result.getFirst();
    assertThat(mentorDto.getImages()).hasSize(1);
    assertThat(mentorDto.getImages().getFirst().path()).isEqualTo(resource.getDriveFileLink());
    assertThat(mentorDto.getImages().getFirst().type()).isEqualTo(ImageType.DESKTOP);
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
    var mentorDto = result.getFirst();
    assertThat(mentorDto.getImages()).isNullOrEmpty();
  }

  @Test
  @DisplayName(
      "Given existing member with email, when creating mentor with same email, then it should use existing member")
  void shouldUseExistingMemberWhenMentorEmailAlreadyExists() {
    var mentor = mock(Mentor.class);
    when(mentor.getEmail()).thenReturn("existing@test.com");
    when(mentor.getFullName()).thenReturn("Existing Member as Mentor");
    when(mentor.getPosition()).thenReturn("Software Engineer");
    when(mentor.getSlackDisplayName()).thenReturn("@existing");
    when(mentor.getCountry()).thenReturn(mock(com.wcc.platform.domain.cms.attributes.Country.class));
    when(mentor.getCity()).thenReturn("New York");
    when(mentor.getCompanyName()).thenReturn("Tech Corp");
    when(mentor.getImages()).thenReturn(List.of());
    when(mentor.getNetwork()).thenReturn(List.of());
    when(mentor.getProfileStatus())
        .thenReturn(com.wcc.platform.domain.platform.member.ProfileStatus.ACTIVE);
    when(mentor.getSkills()).thenReturn(mock(com.wcc.platform.domain.platform.mentorship.Skills.class));
    when(mentor.getSpokenLanguages()).thenReturn(List.of("English"));
    when(mentor.getBio()).thenReturn("Bio");
    when(mentor.getMenteeSection()).thenReturn(
        mock(com.wcc.platform.domain.cms.pages.mentorship.MenteeSection.class));
    when(mentor.getFeedbackSection()).thenReturn(null);
    when(mentor.getResources()).thenReturn(null);

    // Mock existing member with same email
    Member existingMember = Member.builder().id(999L).email("existing@test.com").build();
    when(memberRepository.findByEmail("existing@test.com")).thenReturn(Optional.of(existingMember));

    var mentorWithExistingId = mock(Mentor.class);
    when(mentorWithExistingId.getId()).thenReturn(999L);
    when(mentorRepository.create(any(Mentor.class))).thenReturn(mentorWithExistingId);

    Mentor result = service.create(mentor);

    assertThat(result.getId()).isEqualTo(999L);
    verify(memberRepository).findByEmail("existing@test.com");
    verify(mentorRepository).create(any(Mentor.class));
  }
}
