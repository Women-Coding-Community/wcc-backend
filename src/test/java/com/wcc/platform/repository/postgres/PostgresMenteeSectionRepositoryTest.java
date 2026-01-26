package com.wcc.platform.repository.postgres;

import static com.wcc.platform.repository.postgres.mentorship.PostgresMenteeSectionRepository.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.wcc.platform.domain.cms.pages.mentorship.MenteeSection;
import com.wcc.platform.domain.cms.pages.mentorship.MentorMonthAvailability;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import com.wcc.platform.repository.postgres.mentorship.PostgresMenteeSectionRepository;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

@ExtendWith(MockitoExtension.class)
class PostgresMenteeSectionRepositoryTest {

  @Mock private JdbcTemplate jdbc;

  @InjectMocks private PostgresMenteeSectionRepository menteeSecRepo;

  private MenteeSection menteeSection;
  private Long mentorId;

  @BeforeEach
  void setUp() {
    mentorId = 1L;

    List<MentorshipType> mentorshipTypes = List.of(MentorshipType.LONG_TERM, MentorshipType.AD_HOC);

    List<MentorMonthAvailability> availability =
        List.of(
            new MentorMonthAvailability(Month.JANUARY, 2),
            new MentorMonthAvailability(Month.FEBRUARY, 3));

    menteeSection =
        new MenteeSection(
            mentorshipTypes,
            availability,
            "Ideal mentee description UPDATED",
            "Additional info UPDATED");
  }

  @Test
  void testUpdateMenteeSectionSuccess() {

    menteeSecRepo.updateMenteeSection(menteeSection, mentorId);

    verify(jdbc, times(1))
        .update(
            eq(UPDATE_MENTEE_SECTION),
            eq("Ideal mentee description UPDATED"),
            eq("Additional info UPDATED"),
            eq(mentorId));

    verify(jdbc, times(1)).update(eq(DELETE_MENTOR_TYPES), eq(mentorId));

    verify(jdbc, times(2)).update(eq(INSERT_MENTOR_TYPES), eq(mentorId), anyInt());

    verify(jdbc, times(2))
        .update(
            eq(UPDATE_AVAILABILITY),
            anyInt(), // month_num
            anyInt(), // hours
            eq(mentorId));
  }

  @Test
  void testUpdateMenteeSectionUpdatesTextFields() {
    menteeSecRepo.updateMenteeSection(menteeSection, mentorId);

    verify(jdbc)
        .update(
            eq(UPDATE_MENTEE_SECTION),
            eq("Ideal mentee description UPDATED"),
            eq("Additional info UPDATED"),
            eq(mentorId));
  }

  @Test
  void testUpdateMenteeSectionDeletesOldMentorshipTypes() {
    menteeSecRepo.updateMenteeSection(menteeSection, mentorId);

    verify(jdbc).update(eq(DELETE_MENTOR_TYPES), eq(mentorId));
  }

  @Test
  void testUpdateMenteeSectionInsertsAllMentorshipTypes() {
    menteeSecRepo.updateMenteeSection(menteeSection, mentorId);

    verify(jdbc)
        .update(
            eq(INSERT_MENTOR_TYPES),
            eq(mentorId),
            eq(MentorshipType.LONG_TERM.getMentorshipTypeId()));

    verify(jdbc)
        .update(
            eq(INSERT_MENTOR_TYPES), eq(mentorId), eq(MentorshipType.AD_HOC.getMentorshipTypeId()));
  }

  @Test
  void testUpdateMenteeSectionUpdatesAllAvailability() {
    menteeSecRepo.updateMenteeSection(menteeSection, mentorId);

    verify(jdbc)
        .update(contains(UPDATE_AVAILABILITY), eq(Month.JANUARY.getValue()), eq(2), eq(mentorId));

    verify(jdbc)
        .update(contains(UPDATE_AVAILABILITY), eq(Month.FEBRUARY.getValue()), eq(3), eq(mentorId));
  }

  @Test
  void testUpdateMenteeSectionWithEmptyMentorshipTypes() {
    MenteeSection emptyTypesSection =
        new MenteeSection(
            List.of(), // Empty mentorship types
            List.of(new MentorMonthAvailability(Month.APRIL, 1)),
            "Ideal mentee",
            "Additional");

    menteeSecRepo.updateMenteeSection(emptyTypesSection, mentorId);

    verify(jdbc, times(1)).update(eq(DELETE_MENTOR_TYPES), eq(mentorId));

    verify(jdbc, never()).update(eq(INSERT_MENTOR_TYPES), anyLong(), anyInt());
  }

  @Test
  void testUpdateMenteeSectionWithEmptyAvailability() {
    MenteeSection emptyAvailabilitySection =
        new MenteeSection(
            List.of(MentorshipType.AD_HOC),
            List.of(), // Empty availability
            "Ideal mentee",
            "Additional");

    menteeSecRepo.updateMenteeSection(emptyAvailabilitySection, mentorId);

    verify(jdbc, never()).update(contains(UPDATE_AVAILABILITY), anyInt(), anyInt(), anyLong());
  }

  @Test
  void testUpdateMenteeSectionVerifiesAllSqlStatements() {
    menteeSecRepo.updateMenteeSection(menteeSection, mentorId);

    verify(jdbc).update(eq(UPDATE_MENTEE_SECTION), anyString(), anyString(), anyLong());
    verify(jdbc).update(eq(DELETE_MENTOR_TYPES), anyLong());
    verify(jdbc, atLeastOnce()).update(eq(INSERT_MENTOR_TYPES), anyLong(), anyInt());
    verify(jdbc, atLeastOnce()).update(eq(UPDATE_AVAILABILITY), anyInt(), anyInt(), anyLong());
  }
}
