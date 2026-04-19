package com.wcc.platform.service;

import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.attributes.ImageType;
import com.wcc.platform.domain.cms.pages.mentorship.MentorAppliedFilters;
import com.wcc.platform.domain.cms.pages.mentorship.MentorsPage;
import com.wcc.platform.domain.exceptions.DuplicatedMemberException;
import com.wcc.platform.domain.exceptions.MemberNotFoundException;
import com.wcc.platform.domain.exceptions.MentorStatusException;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.platform.mentorship.CycleStatus;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.domain.platform.mentorship.MentorDto;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleEntity;
import com.wcc.platform.domain.platform.type.RoleType;
import com.wcc.platform.domain.resource.MemberProfilePicture;
import com.wcc.platform.domain.resource.Resource;
import com.wcc.platform.repository.MemberProfilePictureRepository;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.MentorRepository;
import com.wcc.platform.repository.MentorshipCycleRepository;
import com.wcc.platform.utils.FiltersUtil;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** Platform Service. */
@Slf4j
@Service
@AllArgsConstructor
@SuppressWarnings({
  "PMD.ExcessiveImports",
  "PMD.TooManyMethods"
}) // TODO: https://github.com/Women-Coding-Community/wcc-backend/issues/520
public class MentorshipService {

  public static final MentorshipCycleEntity CLOSED_CYCLE =
      MentorshipCycleEntity.builder().status(CycleStatus.CLOSED).build();

  private static final int MINIMUM_HOURS = 2;
  private final MentorRepository mentorRepository;
  private final MemberRepository memberRepository;
  private final MentorshipCycleRepository cycleRepository;
  private final UserProvisionService userProvisionService;
  private final MemberProfilePictureRepository profilePicRepo;
  private final MentorshipNotificationService notificationService;

  /**
   * Create a mentor record.
   *
   * @return Mentor record created successfully.
   */
  public Mentor create(final Mentor mentor) {
    final var existingMember = memberRepository.findByEmail(mentor.getEmail());

    if (existingMember.isPresent()) {
      final var existingMemberId = existingMember.get().getId();
      final var mentorWithExistingId =
          Mentor.mentorBuilder()
              .id(existingMemberId)
              .fullName(mentor.getFullName())
              .position(mentor.getPosition())
              .email(mentor.getEmail())
              .slackDisplayName(mentor.getSlackDisplayName())
              .country(mentor.getCountry())
              .city(mentor.getCity())
              .companyName(mentor.getCompanyName())
              .images(mentor.getImages())
              .network(mentor.getNetwork())
              .pronouns(mentor.getPronouns())
              .pronounCategory(mentor.getPronounCategory())
              .profileStatus(ProfileStatus.PENDING)
              .skills(mentor.getSkills())
              .spokenLanguages(mentor.getSpokenLanguages())
              .bio(mentor.getBio())
              .menteeSection(mentor.getMenteeSection())
              .feedbackSection(mentor.getFeedbackSection())
              .resources(mentor.getResources())
              .isWomen(mentor.getIsWomen())
              .calendlyLink(mentor.getCalendlyLink())
              .acceptMale(mentor.getAcceptMale())
              .acceptPromotion(mentor.getAcceptPromotion())
              .build();

      return mentorRepository.create(mentorWithExistingId);
    }

    if (mentor.getId() != null) {
      final Optional<Mentor> mentorExists = mentorRepository.findById(mentor.getId());
      if (mentorExists.isPresent()) {
        throw new DuplicatedMemberException(mentorExists.get().getEmail());
      }
    }
    validateMentorCommitment(mentor);
    final var mentorCreated = mentorRepository.create(mentor);
    if (mentorRepository.findById(mentorCreated.getId()).isPresent()) {
      userProvisionService.provisionUserRole(
          mentorCreated.getId(), mentorCreated.getEmail(), RoleType.MENTOR);
    }
    return enrichMentorWithProfilePicture(mentorCreated);
  }

  /**
   * Return all stored mentors.
   *
   * @return List of mentors.
   */
  public MentorsPage getMentorsPage(
      final MentorsPage mentorsPage, final MentorAppliedFilters filters) {
    final var currentCycle = getCurrentCycle();

    final var mentors = FiltersUtil.applyFilters(getAllActiveMentors(currentCycle), filters);

    return mentorsPage.updateUpdate(
        currentCycle.toOpenCycleValue(), FiltersUtil.mentorshipAllFilters(), mentors);
  }

  public MentorsPage getMentorsPage(final MentorsPage mentorsPage) {
    return getMentorsPage(mentorsPage, null);
  }

  /**
   * Return all mentors ignoring their status and the current cycle. Intended for privileged
   * (admin/leader) use only.
   *
   * @return list of all mentor DTOs regardless of {@link ProfileStatus}.
   */
  public List<MentorDto> getAllMentors() {
    return mentorRepository.getAll().stream()
        .map(mentor -> enrichWithProfilePicture(mentor.toDto()))
        .toList();
  }

  /**
   * Retrieves the mentorship cycle for the given mentorship type and cycle year. If no open cycle
   * is found, returns a default closed cycle.
   *
   * @return The MentorshipCycleEntity
   */
  public MentorshipCycleEntity getCurrentCycle() {
    final var openCycle = cycleRepository.findOpenCycle();

    return openCycle.orElse(CLOSED_CYCLE);
  }

  /**
   * Return all ACTIVE mentors in the current cycle. Mentors with PENDING or REJECTED status are
   * always excluded regardless of the cycle state.
   *
   * @return List of active mentor DTOs.
   */
  public List<MentorDto> getAllActiveMentors() {
    return getAllActiveMentors(getCurrentCycle());
  }

  private List<MentorDto> getAllActiveMentors(final MentorshipCycleEntity currentCycle) {
    final var allActiveMentors =
        mentorRepository.getAll().stream()
            .filter(m -> m.getProfileStatus() == ProfileStatus.ACTIVE);

    if (currentCycle.getStatus() == CycleStatus.CLOSED) {
      return allActiveMentors.map(mentor -> enrichWithProfilePicture(mentor.toDto())).toList();
    }

    return allActiveMentors
        .map(mentor -> enrichWithProfilePicture(mentor.toDto(currentCycle.toMentorshipCycle())))
        .toList();
  }

  private MentorDto enrichWithProfilePicture(final MentorDto dto) {
    final Optional<Image> profilePicture = fetchProfilePicture(dto.getId());

    if (profilePicture.isEmpty()) {
      return dto;
    }

    return MentorDto.mentorDtoBuilder()
        .id(dto.getId())
        .fullName(dto.getFullName())
        .position(dto.getPosition())
        .country(dto.getCountry())
        .city(dto.getCity())
        .companyName(dto.getCompanyName())
        .images(List.of(profilePicture.get()))
        .network(dto.getNetwork())
        .pronouns(dto.getPronouns())
        .pronounCategory(dto.getPronounCategory())
        .skills(dto.getSkills())
        .spokenLanguages(dto.getSpokenLanguages())
        .bio(dto.getBio())
        .menteeSection(dto.getMenteeSection())
        .feedbackSection(dto.getFeedbackSection())
        .resources(dto.getResources())
        .build();
  }

  private Mentor enrichMentorWithProfilePicture(final Mentor mentor) {
    final Optional<Image> profilePicture = fetchProfilePicture(mentor.getId());

    if (profilePicture.isEmpty()) {
      return mentor;
    }

    return Mentor.mentorBuilder()
        .id(mentor.getId())
        .fullName(mentor.getFullName())
        .position(mentor.getPosition())
        .email(mentor.getEmail())
        .slackDisplayName(mentor.getSlackDisplayName())
        .country(mentor.getCountry())
        .city(mentor.getCity())
        .companyName(mentor.getCompanyName())
        .images(List.of(profilePicture.get()))
        .network(mentor.getNetwork())
        .pronouns(mentor.getPronouns())
        .pronounCategory(mentor.getPronounCategory())
        .profileStatus(mentor.getProfileStatus())
        .skills(mentor.getSkills())
        .spokenLanguages(mentor.getSpokenLanguages())
        .bio(mentor.getBio())
        .menteeSection(mentor.getMenteeSection())
        .feedbackSection(mentor.getFeedbackSection())
        .resources(mentor.getResources())
        .isWomen(mentor.getIsWomen())
        .calendlyLink(mentor.getCalendlyLink())
        .acceptMale(mentor.getAcceptMale())
        .acceptPromotion(mentor.getAcceptPromotion())
        .build();
  }

  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  private Optional<Image> fetchProfilePicture(final Long memberId) {
    try {
      return profilePicRepo
          .findByMemberId(memberId)
          .map(MemberProfilePicture::getResource)
          .map(this::convertResourceToImage);
    } catch (Exception e) {
      // Catching generic exception intentionally to ensure profile picture fetch
      // failures don't break the entire mentor retrieval operation
      log.warn("Failed to fetch profile picture for member {}: {}", memberId, e.getMessage());
      return Optional.empty();
    }
  }

  private Image convertResourceToImage(final Resource resource) {
    return new Image(
        resource.getDriveFileLink(),
        resource.getName().isEmpty() ? "Profile picture" : resource.getName(),
        ImageType.DESKTOP);
  }

  /**
   * Update a mentor record.
   *
   * @param mentorId mentor's unique identifier
   * @param mentorDto MentorDto with updated member's data
   * @return Mentor record updated successfully.
   */
  public Mentor updateMentor(final Long mentorId, final MentorDto mentorDto) {
    if (mentorDto.getId() != null && !mentorId.equals(mentorDto.getId())) {
      throw new IllegalArgumentException("Mentor ID does not match the provided mentorId");
    }

    final Optional<Mentor> mentorOptional = mentorRepository.findById(mentorId);
    final var mentor = mentorOptional.orElseThrow(() -> new MemberNotFoundException(mentorId));

    final Mentor updatedMentor = mentorDto.merge(mentor);
    validateMentorCommitment(updatedMentor);
    final Mentor result = mentorRepository.update(mentorId, updatedMentor);
    return enrichMentorWithProfilePicture(result);
  }

  /**
   * Activate a pending mentor by setting their status to ACTIVE.
   *
   * @param mentorId mentor's unique identifier
   * @return mentor with active status
   * @throws MemberNotFoundException if mentor is not found
   * @throws MentorStatusException if mentor is already active
   */
  public Mentor activateMentor(final Long mentorId) {
    final Optional<Mentor> mentorOptional = mentorRepository.findById(mentorId);
    final var mentor = mentorOptional.orElseThrow(() -> new MemberNotFoundException(mentorId));

    if (mentor.getProfileStatus() == ProfileStatus.ACTIVE) {
      throw new MentorStatusException("Mentor with ID " + mentorId + " is already active");
    }

    final Mentor activatedMentor =
        mentorRepository.updateProfileStatus(mentorId, ProfileStatus.ACTIVE);

    notificationService.sendMentorApprovalEmail(activatedMentor);

    return activatedMentor;
  }

  /**
   * Reject a pending mentor by setting their status to REJECTED.
   *
   * @param mentorId mentor's unique identifier
   * @return mentor with a rejected status
   * @throws MemberNotFoundException if mentor is not found
   * @throws MentorStatusException if mentor is already rejected
   */
  public Mentor rejectMentor(final Long mentorId, final String rejectionReason) {
    final Optional<Mentor> mentorOptional = mentorRepository.findById(mentorId);
    final var mentor = mentorOptional.orElseThrow(() -> new MemberNotFoundException(mentorId));

    if (mentor.getProfileStatus() == ProfileStatus.REJECTED) {
      throw new MentorStatusException("Mentor with ID " + mentorId + " is already rejected");
    }

    final Mentor rejectedMentor =
        mentorRepository.updateToRejected(mentorId, ProfileStatus.REJECTED, rejectionReason);

    notificationService.sendMentorRejectionEmail(rejectedMentor, rejectionReason);

    return rejectedMentor;
  }

  private void validateMentorCommitment(final Mentor mentor) {
    final var menteeSection = mentor.getMenteeSection();

    if (menteeSection.longTerm() != null) {
      final var longTerm = menteeSection.longTerm();
      final int hoursPerMentee = longTerm.hours() / longTerm.numMentee();
      if (hoursPerMentee < MINIMUM_HOURS) {
        throw new IllegalArgumentException(
            "Long-term mentorship requires at least 2 hours per mentee.");
      }
    }
  }
}
