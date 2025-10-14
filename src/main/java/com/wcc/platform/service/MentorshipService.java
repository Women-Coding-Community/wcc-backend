package com.wcc.platform.service;

import com.wcc.platform.domain.cms.pages.mentorship.MentorsPage;
import com.wcc.platform.domain.exceptions.DuplicatedMemberException;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.domain.platform.mentorship.MentorDto;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycle;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import com.wcc.platform.repository.MentorRepository;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Platform Service. */
@Service
public class MentorshipService {

  /* package */ static final MentorshipCycle CYCLE_CLOSED = new MentorshipCycle(null, null);

  private static final String EUROPE_LONDON = "Europe/London";
  private static final MentorshipCycle ACTIVE_LONG_TERM =
      new MentorshipCycle(MentorshipType.LONG_TERM, Month.MARCH);

  private final MentorRepository mentorRepository;
  private final int daysCycleOpen;

  @Autowired
  public MentorshipService(
      final MentorRepository mentorRepository,
      final @Value("${mentorship.daysCycleOpen}") int daysCycleOpen) {
    this.mentorRepository = mentorRepository;
    this.daysCycleOpen = daysCycleOpen;
  }

  /**
   * Create a mentor record.
   *
   * @return Mentor record created successfully.
   */
  public Mentor create(final Mentor mentor) {
    final Optional<Mentor> mentorExists = mentorRepository.findById(mentor.getId());

    if (mentorExists.isPresent()) {
      throw new DuplicatedMemberException(mentorExists.get().getEmail());
    }
    return mentorRepository.create(mentor);
  }

  /**
   * Return all stored mentors.
   *
   * @return List of mentors.
   */
  public MentorsPage getMentorsPage(final MentorsPage mentorsPage) {
    final var currentCycle = getCurrentCycle();
    final var allMentors = getAllMentors(currentCycle);

    return mentorsPage.updateUpdate(currentCycle.toOpenCycle(), allMentors);
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
      return allMentors.stream().map(Mentor::toDto).toList();
    }

    return allMentors.stream().map(mentor -> mentor.toDto(currentCycle)).toList();
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
}
