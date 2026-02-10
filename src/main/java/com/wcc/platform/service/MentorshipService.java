package com.wcc.platform.service;

import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.attributes.ImageType;
import com.wcc.platform.domain.cms.pages.mentorship.MentorAppliedFilters;
import com.wcc.platform.domain.cms.pages.mentorship.MentorsPage;
import com.wcc.platform.domain.exceptions.DuplicatedMemberException;
import com.wcc.platform.domain.exceptions.MemberNotFoundException;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.domain.platform.mentorship.MentorDto;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycle;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import com.wcc.platform.domain.resource.MemberProfilePicture;
import com.wcc.platform.domain.resource.Resource;
import com.wcc.platform.repository.MemberProfilePictureRepository;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.MentorRepository;
import com.wcc.platform.utils.FiltersUtil;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Platform Service. */
@Slf4j
@Service
public class MentorshipService {

  /* package */ static final MentorshipCycle CYCLE_CLOSED = new MentorshipCycle(null, null);

  private static final String EUROPE_LONDON = "Europe/London";
  private static final MentorshipCycle ACTIVE_LONG_TERM =
      new MentorshipCycle(MentorshipType.LONG_TERM, Month.MARCH);
  private static final int MINIMUM_HOURS = 2;

  private final MentorRepository mentorRepository;
  private final MemberRepository memberRepository;
  private final MemberProfilePictureRepository profilePicRepo;
  private final int daysCycleOpen;

  @Autowired
  public MentorshipService(
      final MentorRepository mentorRepository,
      final MemberRepository memberRepository,
      final MemberProfilePictureRepository profilePicRepo,
      final @Value("${mentorship.daysCycleOpen}") int daysCycleOpen) {
    this.mentorRepository = mentorRepository;
    this.memberRepository = memberRepository;
    this.profilePicRepo = profilePicRepo;
    this.daysCycleOpen = daysCycleOpen;
  }

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
              .profileStatus(ProfileStatus.PENDING)
              .skills(mentor.getSkills())
              .spokenLanguages(mentor.getSpokenLanguages())
              .bio(mentor.getBio())
              .menteeSection(mentor.getMenteeSection())
              .feedbackSection(mentor.getFeedbackSection())
              .resources(mentor.getResources())
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
    return mentorRepository.create(mentor);
  }

  /**
   * Return all stored mentors.
   *
   * @return List of mentors.
   */
  public MentorsPage getMentorsPage(
      final MentorsPage mentorsPage, final MentorAppliedFilters filters) {
    final var currentCycle = getCurrentCycle();

    final var mentors = FiltersUtil.applyFilters(getAllMentors(currentCycle), filters);

    return mentorsPage.updateUpdate(
        currentCycle.toOpenCycle(), FiltersUtil.mentorshipAllFilters(), mentors);
  }

  public MentorsPage getMentorsPage(final MentorsPage mentorsPage) {
    return getMentorsPage(mentorsPage, null);
  }

  /**
   * Return all stored mentors in the current cycle.
   *
   * @return List of mentors.
   */
  public List<MentorDto> getAllMentors() {
    return getAllMentors(getCurrentCycle());
  }

  private List<MentorDto> getAllMentors(final MentorshipCycle currentCycle) {
    final var allMentors = mentorRepository.getAll();

    if (currentCycle == CYCLE_CLOSED) {
      return allMentors.stream().map(mentor -> enrichWithProfilePicture(mentor.toDto())).toList();
    }

    return allMentors.stream()
        .map(mentor -> enrichWithProfilePicture(mentor.toDto(currentCycle)))
        .toList();
  }

  /* package */ MentorshipCycle getCurrentCycle() {
    final ZonedDateTime londonTime = nowLondon();
    final LocalDate currentDate = londonTime.toLocalDate();

    final var currentMonth = currentDate.getMonth();
    final int dayOfMonth = currentDate.getDayOfMonth();

    if (currentMonth == Month.MARCH && dayOfMonth <= daysCycleOpen) {
      return ACTIVE_LONG_TERM;
    }

    if (currentMonth.getValue() >= Month.MAY.getValue()
        && currentMonth.getValue() <= Month.NOVEMBER.getValue()
        && dayOfMonth <= daysCycleOpen) {
      return new MentorshipCycle(MentorshipType.AD_HOC, currentMonth);
    }

    return CYCLE_CLOSED;
  }

  /* package */ ZonedDateTime nowLondon() {
    return ZonedDateTime.now(ZoneId.of(EUROPE_LONDON));
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
        .availability(dto.getAvailability())
        .skills(dto.getSkills())
        .spokenLanguages(dto.getSpokenLanguages())
        .bio(dto.getBio())
        .menteeSection(dto.getMenteeSection())
        .feedbackSection(dto.getFeedbackSection())
        .resources(dto.getResources())
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
    return mentorRepository.update(mentorId, updatedMentor);
  }

  /**
   * Activate a pending mentor by setting their status to ACTIVE.
   *
   * @param mentorId mentor's unique identifier
   * @return mentor with active status
   * @throws MemberNotFoundException if mentor is not found
   * @throws IllegalStateException if mentor is already active
   */
  public Mentor activateMentor(final Long mentorId) {
    final Optional<Mentor> mentorOptional = mentorRepository.findById(mentorId);
    final var mentor = mentorOptional.orElseThrow(() -> new MemberNotFoundException(mentorId));

    if (mentor.getProfileStatus() == ProfileStatus.ACTIVE) {
      throw new IllegalStateException("Mentor with ID " + mentorId + " is already active");
    }

    return mentorRepository.updateProfileStatus(mentorId, ProfileStatus.ACTIVE);
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
