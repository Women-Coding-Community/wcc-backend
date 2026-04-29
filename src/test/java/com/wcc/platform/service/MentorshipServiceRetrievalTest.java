package com.wcc.platform.service;

import static com.wcc.platform.factories.SetupMentorFactories.createMemberProfilePictureTest;
import static com.wcc.platform.factories.SetupMentorFactories.createResourceTest;
import static com.wcc.platform.service.MentorshipService.CLOSED_CYCLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import com.wcc.platform.domain.cms.attributes.ImageType;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.platform.mentorship.CycleStatus;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.domain.platform.mentorship.MentorDto;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleEntity;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import com.wcc.platform.repository.MemberProfilePictureRepository;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.MentorRepository;
import com.wcc.platform.repository.MentorshipCycleRepository;
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
class MentorshipServiceRetrievalTest {

  @Mock private MentorRepository mentorRepository;
  @Mock private MemberRepository memberRepository;
  @Mock private MentorshipCycleRepository cycleRepository;
  @Mock private UserProvisionService userProvisionService;
  @Mock private MemberProfilePictureRepository profilePicRepo;
  @Mock private MentorshipNotificationService notificationService;
  @Mock private ResourceService resourceService;
  private MentorshipService service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    service =
        spy(
            new MentorshipService(
                mentorRepository,
                memberRepository,
                cycleRepository,
                userProvisionService,
                profilePicRepo,
                notificationService,
                resourceService));
  }

  @Test
  void whenGetAllMentorsGivenCycleClosedThenReturnDtos() {
    var mentor = mock(Mentor.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
    var dto = mock(MentorDto.class);
    when(mentor.getProfileStatus()).thenReturn(ProfileStatus.ACTIVE);
    when(mentor.toDto()).thenReturn(dto);
    when(mentorRepository.getAll()).thenReturn(List.of(mentor));
    when(profilePicRepo.findByMemberId(1L)).thenReturn(Optional.empty());

    doReturn(CLOSED_CYCLE).when(service).getCurrentCycle();

    var result = service.getAllActiveMentors();

    assertThat(result).hasSize(1);
  }

  @Test
  void whenGetAllMentorsGivenAdHocCycleOpenThenReturnDtos() {
    var mentor = mock(Mentor.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
    var dto = mock(MentorDto.class);
    when(mentor.getProfileStatus()).thenReturn(ProfileStatus.ACTIVE);
    when(mentor.toDto(any())).thenReturn(dto);
    when(dto.getId()).thenReturn(1L);
    when(mentorRepository.getAll()).thenReturn(List.of(mentor));

    var cycle =
        MentorshipCycleEntity.builder()
            .mentorshipType(MentorshipType.AD_HOC)
            .cycleMonth(Month.MAY)
            .build();
    doReturn(cycle).when(service).getCurrentCycle();

    var result = service.getAllActiveMentors();

    assertThat(result).hasSize(1);
  }

  @Test
  void whenGetAllMentorsGivenLongTermCycleOpenThenReturnDtos() {
    var mentor = mock(Mentor.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
    var dto = mock(MentorDto.class);
    when(mentor.getProfileStatus()).thenReturn(ProfileStatus.ACTIVE);
    when(mentor.toDto(any())).thenReturn(dto);
    when(dto.getId()).thenReturn(1L);
    when(mentorRepository.getAll()).thenReturn(List.of(mentor));
    when(profilePicRepo.findByMemberId(1L)).thenReturn(Optional.empty());

    var cycle =
        MentorshipCycleEntity.builder()
            .mentorshipType(MentorshipType.LONG_TERM)
            .cycleMonth(Month.MARCH)
            .build();
    doReturn(cycle).when(service).getCurrentCycle();

    var result = service.getAllActiveMentors();

    assertThat(result).hasSize(1);
  }

  private <T> T any() {
    return org.mockito.ArgumentMatchers.any();
  }

  @Test
  void testGetCurrentCycleReturnsOpenCycle() {
    var cycle =
        MentorshipCycleEntity.builder()
            .mentorshipType(MentorshipType.LONG_TERM)
            .cycleMonth(Month.MARCH)
            .status(CycleStatus.OPEN)
            .build();
    when(cycleRepository.findOpenCycle()).thenReturn(Optional.of(cycle));

    var result = service.getCurrentCycle();

    assertEquals(MentorshipType.LONG_TERM, result.getMentorshipType());
    assertEquals(Month.MARCH, result.getCycleMonth());
  }

  @Test
  void testGetCurrentCycleReturnsClosedWhenNoOpenCycle() {
    when(cycleRepository.findOpenCycle()).thenReturn(Optional.empty());

    try {
      service.getCurrentCycle();
    } catch (com.wcc.platform.domain.exceptions.MentorshipCycleClosedException e) {
      assertEquals("Mentorship cycle is closed", e.getMessage());
    }
  }

  @Test
  @DisplayName(
      "Given mentor with profile picture, when getAllMentors is called, then images list should"
          + " contain profile picture")
  void shouldMergeProfilePictureIntoImagesWhenMentorHasProfilePicture() {
    var mentor = mock(Mentor.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
    var dto = mock(MentorDto.class);
    when(mentor.getProfileStatus()).thenReturn(ProfileStatus.ACTIVE);
    when(mentor.toDto()).thenReturn(dto);
    when(dto.getId()).thenReturn(1L);
    when(mentorRepository.getAll()).thenReturn(List.of(mentor));
    when(profilePicRepo.findByMemberId(1L)).thenReturn(Optional.empty());

    var resource = createResourceTest();
    var profilePicture = createMemberProfilePictureTest(1L).toBuilder().resource(resource).build();
    when(profilePicRepo.findByMemberId(1L)).thenReturn(Optional.of(profilePicture));

    doReturn(CLOSED_CYCLE).when(service).getCurrentCycle();

    var result = service.getAllActiveMentors();

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
    when(mentor.getProfileStatus()).thenReturn(ProfileStatus.ACTIVE);
    when(mentor.toDto()).thenReturn(dto);
    when(dto.getId()).thenReturn(1L);
    when(mentorRepository.getAll()).thenReturn(List.of(mentor));
    when(profilePicRepo.findByMemberId(1L)).thenReturn(Optional.empty());

    doReturn(CLOSED_CYCLE).when(service).getCurrentCycle();

    var result = service.getAllActiveMentors();

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
    when(mentor.getProfileStatus()).thenReturn(ProfileStatus.ACTIVE);
    when(mentor.toDto()).thenReturn(dto);
    when(dto.getId()).thenReturn(1L);
    when(mentorRepository.getAll()).thenReturn(List.of(mentor));
    when(profilePicRepo.findByMemberId(1L)).thenThrow(new RuntimeException("Database error"));

    doReturn(CLOSED_CYCLE).when(service).getCurrentCycle();

    var result = service.getAllActiveMentors();

    assertThat(result).hasSize(1);
    var mentorDtoResult = result.getFirst();
    assertThat(mentorDtoResult.getImages()).isNullOrEmpty();
  }

  @Test
  @DisplayName(
      "Given mentor with pronouns, when enriched with profile picture, then pronouns should be preserved")
  void shouldPreservePronounsWhenEnrichedWithProfilePicture() {
    var mentor = mock(Mentor.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
    var dto = mock(MentorDto.class);
    when(mentor.getProfileStatus()).thenReturn(ProfileStatus.ACTIVE);
    when(mentor.toDto()).thenReturn(dto);
    when(dto.getId()).thenReturn(1L);
    when(dto.getPronouns()).thenReturn("they/them");
    when(dto.getPronounCategory())
        .thenReturn(com.wcc.platform.domain.cms.attributes.PronounCategory.NEUTRAL);
    when(mentorRepository.getAll()).thenReturn(List.of(mentor));

    var resource = createResourceTest();
    var profilePicture = createMemberProfilePictureTest(1L).toBuilder().resource(resource).build();
    when(profilePicRepo.findByMemberId(1L)).thenReturn(Optional.of(profilePicture));

    doReturn(CLOSED_CYCLE).when(service).getCurrentCycle();

    var result = service.getAllActiveMentors();

    assertThat(result).hasSize(1);
    var mentorDtoResult = result.getFirst();
    assertThat(mentorDtoResult.getPronouns()).isEqualTo("they/them");
    assertThat(mentorDtoResult.getPronounCategory())
        .isEqualTo(com.wcc.platform.domain.cms.attributes.PronounCategory.NEUTRAL);
  }
}
