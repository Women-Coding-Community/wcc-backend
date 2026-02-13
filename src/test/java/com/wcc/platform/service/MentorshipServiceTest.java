package com.wcc.platform.service;

import static com.wcc.platform.factories.SetupMentorFactories.createMentorDtoTest;
import static com.wcc.platform.factories.SetupMentorFactories.createMentorTest;
import static com.wcc.platform.factories.SetupMentorFactories.createUpdatedMentorTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.cms.pages.mentorship.LongTermMentorship;
import com.wcc.platform.domain.cms.pages.mentorship.MenteeSection;
import com.wcc.platform.domain.cms.pages.mentorship.MentorMonthAvailability;
import com.wcc.platform.domain.exceptions.DuplicatedMemberException;
import com.wcc.platform.domain.exceptions.MemberNotFoundException;
import com.wcc.platform.domain.exceptions.MentorStatusException;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.domain.platform.mentorship.MentorDto;
import com.wcc.platform.domain.platform.type.MemberType;
import com.wcc.platform.repository.MemberProfilePictureRepository;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.MentorRepository;
import java.time.Month;
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
  @Mock private NotificationService notificationService;
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
    final int daysOpen = 10;
    MockitoAnnotations.openMocks(this);
    mentor = createMentorTest();
    mentorDto = createMentorDtoTest(1L, MemberType.DIRECTOR);
    updatedMentor = createUpdatedMentorTest(mentor, mentorDto);
    service =
        spy(
            new MentorshipService(
                mentorRepository, memberRepository, profilePicRepo, daysOpen, notificationService));
  }

  @Test
  void whenCreateGivenMentorAlreadyExistsThenThrowDuplicatedMemberException() {
    var mentor = mock(Mentor.class);
    when(mentor.getId()).thenReturn(1L);
    when(mentorRepository.findById(1L)).thenReturn(Optional.of(mentor));
    when(mentor.getEmail()).thenReturn("test@test.com");
    when(memberRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());

    assertThrows(DuplicatedMemberException.class, () -> service.create(mentor));
    verify(mentorRepository, never()).create(any());
  }

  @Test
  void whenCreateGivenMentorDoesNotExistThenCreateMentor() {
    var mentor = mock(Mentor.class);
    var menteeSection = mock(MenteeSection.class);
    when(mentor.getId()).thenReturn(2L);
    when(mentor.getMenteeSection()).thenReturn(menteeSection);
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
  @DisplayName(
      "Given mentor with long-term mentorship and 4+ hours commitment for 1 mentee When creating"
          + " Then create mentor and return it")
  void testCreateAvailableLongTermMentor() {
    var mentor = mock(Mentor.class);
    var menteeSection = mock(MenteeSection.class);
    when(mentor.getId()).thenReturn(2L);
    when(mentor.getMenteeSection()).thenReturn(menteeSection);
    when(menteeSection.longTerm()).thenReturn(new LongTermMentorship(1, 4));
    when(mentorRepository.findById(2L)).thenReturn(Optional.empty());
    when(mentorRepository.create(mentor)).thenReturn(mentor);

    var result = service.create(mentor);

    assertEquals(mentor, result);
    verify(mentorRepository).create(mentor);
  }

  @Test
  @DisplayName(
      "Given mentor with ad-hoc mentorship only When creating Then create mentor and return it")
  void testCreateAdHocOnlyMentor() {
    var mentor = mock(Mentor.class);
    var menteeSection = mock(MenteeSection.class);
    when(mentor.getId()).thenReturn(2L);
    when(mentor.getMenteeSection()).thenReturn(menteeSection);
    when(menteeSection.longTerm()).thenReturn(null);
    when(mentorRepository.findById(2L)).thenReturn(Optional.empty());
    when(mentorRepository.create(mentor)).thenReturn(mentor);

    var result = service.create(mentor);

    assertEquals(mentor, result);
    verify(mentorRepository).create(mentor);
  }

  @Test
  @DisplayName(
      "Given mentor with long-term mentorship and under 2 hours per mentee When creating Then throw"
          + " IllegalArgumentException")
  void testCreateUnavailableLongTermMentor() {
    var mentor = mock(Mentor.class);
    var menteeSection = mock(MenteeSection.class);
    when(mentor.getId()).thenReturn(2L);
    when(mentor.getMenteeSection()).thenReturn(menteeSection);
    // 2 mentees with only 2 total hours = 1 hour per mentee (below minimum)
    when(menteeSection.longTerm()).thenReturn(new LongTermMentorship(2, 2));
    when(mentorRepository.findById(2L)).thenReturn(Optional.empty());

    var expectedMsg = "Long-term mentorship requires at least 2 hours per mentee.";
    var exception = assertThrows(IllegalArgumentException.class, () -> service.create(mentor));
    assertEquals(expectedMsg, exception.getMessage());

    verify(mentorRepository, never()).create(any());
  }

  @Test
  @DisplayName(
      "Given mentor with long-term mentorship and exactly 2 hours per mentee When creating Then"
          + " create mentor and return it")
  void testCreateLongTermMentorWithMinimumHours() {
    var mentor = mock(Mentor.class);
    var menteeSection = mock(MenteeSection.class);
    when(mentor.getId()).thenReturn(2L);
    when(mentor.getMenteeSection()).thenReturn(menteeSection);
    when(menteeSection.longTerm()).thenReturn(new LongTermMentorship(2, 4));
    when(mentorRepository.findById(2L)).thenReturn(Optional.empty());
    when(mentorRepository.create(mentor)).thenReturn(mentor);

    var result = service.create(mentor);

    assertEquals(mentor, result);
    verify(mentorRepository).create(mentor);
  }

  @Test
  @DisplayName(
      "Given existing member with email, when creating mentor with same email, then it should use"
          + " existing member")
  void shouldUseExistingMemberWhenMentorEmailAlreadyExists() {
    var mentor = mock(Mentor.class);
    when(mentor.getEmail()).thenReturn("existing@test.com");
    when(mentor.getFullName()).thenReturn("Existing Member as Mentor");
    when(mentor.getPosition()).thenReturn("Software Engineer");
    when(mentor.getSlackDisplayName()).thenReturn("@existing");
    when(mentor.getCountry())
        .thenReturn(mock(com.wcc.platform.domain.cms.attributes.Country.class));
    when(mentor.getCity()).thenReturn("New York");
    when(mentor.getCompanyName()).thenReturn("Tech Corp");
    when(mentor.getImages()).thenReturn(List.of());
    when(mentor.getNetwork()).thenReturn(List.of());
    when(mentor.getProfileStatus())
        .thenReturn(com.wcc.platform.domain.platform.member.ProfileStatus.ACTIVE);
    when(mentor.getSkills())
        .thenReturn(mock(com.wcc.platform.domain.platform.mentorship.Skills.class));
    when(mentor.getSpokenLanguages()).thenReturn(List.of("English"));
    when(mentor.getBio()).thenReturn("Bio");
    when(mentor.getMenteeSection()).thenReturn(mock(MenteeSection.class));
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

  @Test
  @DisplayName(
      "Given mentor exists When updating the mentor Then should update mentor attributes and return"
          + " updated mentor")
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
      "Given mentor does not exist When updating the mentor Then should throw"
          + " MemberNotFoundException")
  void testUpdateMentorNotFound() {
    long mentorId = 1L;

    when(mentorRepository.findById(mentorId)).thenReturn(Optional.empty());

    assertThrows(MemberNotFoundException.class, () -> service.updateMentor(mentorId, mentorDto));

    verify(mentorRepository).findById(mentorId);
    verify(mentorRepository, never()).update(anyLong(), any());
  }

  @Test
  @DisplayName(
      "Given mentor exists When updating with mismatched mentor ID Then should throw"
          + " IllegalArgumentException")
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
      "Given mentor with long-term mentorship and 4+ hours per mentee When updating the mentor"
          + " Then update and return it")
  void testUpdateLongTermMentorAvailableHours() {
    final var updatedMentor =
        createUpdatedMentorTest(
            mentor,
            mentorDto,
            new LongTermMentorship(2, 8), // 4 hours per mentee
            List.of(new MentorMonthAvailability(Month.JANUARY, 2)));
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
      "Given mentor with ad-hoc mentorship only When updating the mentor Then update and return it")
  void testUpdateAdHocOnlyMentor() {
    final var updatedMentor =
        createUpdatedMentorTest(
            mentor,
            mentorDto,
            null, // No long-term
            List.of(
                new MentorMonthAvailability(Month.JANUARY, 1),
                new MentorMonthAvailability(Month.FEBRUARY, 0)));
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
      "Given mentor with long-term mentorship and under 2 hours per mentee When updating the mentor"
          + " Then throw IllegalArgumentException")
  void testUpdateUnavailableLongTermMentorIllegalArgumentException() {
    long mentorId = 1L;
    // 2 mentees with only 2 total hours = 1 hour per mentee (below minimum)
    MentorDto newMentorDto =
        createMentorDtoTest(
            1L,
            MemberType.DIRECTOR,
            new LongTermMentorship(2, 2),
            List.of(new MentorMonthAvailability(Month.JANUARY, 2)));
    when(mentorRepository.findById(mentorId)).thenReturn(Optional.of(mentor));

    var expectedMsg = "Long-term mentorship requires at least 2 hours per mentee.";
    var exception =
        assertThrows(
            IllegalArgumentException.class, () -> service.updateMentor(mentorId, newMentorDto));
    assertEquals(expectedMsg, exception.getMessage());

    verify(mentorRepository).findById(anyLong());
    verify(mentorRepository, never()).update(anyLong(), any());
  }
}
