package com.wcc.platform.repository.postgres;

import static com.wcc.platform.repository.postgres.mentorship.PostgresMenteeSectionRepository.DELETE_AD_HOC;
import static com.wcc.platform.repository.postgres.mentorship.PostgresMenteeSectionRepository.INSERT_AD_HOC;
import static com.wcc.platform.repository.postgres.mentorship.PostgresMenteeSectionRepository.UPDATE_MENTEE_SECTION;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.wcc.platform.domain.cms.pages.mentorship.LongTermMentorship;
import com.wcc.platform.domain.cms.pages.mentorship.MenteeSection;
import com.wcc.platform.domain.cms.pages.mentorship.MentorMonthAvailability;
import com.wcc.platform.repository.postgres.mentorship.PostgresMenteeSectionRepository;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

    List<MentorMonthAvailability> adHocAvailability =
        List.of(
            new MentorMonthAvailability(Month.JANUARY, 2),
            new MentorMonthAvailability(Month.FEBRUARY, 3));

    menteeSection =
        new MenteeSection(
            "Ideal mentee description UPDATED",
            "Additional info UPDATED",
            new LongTermMentorship(2, 8),
            adHocAvailability);
  }

  @Test
  @DisplayName(
      "Given mentee section with long-term and ad-hoc, when updating, then all fields should be"
          + " updated")
  void testUpdateMenteeSectionSuccess() {
    menteeSecRepo.updateMenteeSection(menteeSection, mentorId);

    verify(jdbc, times(1))
        .update(
            eq(UPDATE_MENTEE_SECTION),
            eq("Ideal mentee description UPDATED"),
            eq("Additional info UPDATED"),
            eq(2),
            eq(8),
            eq(mentorId));

    verify(jdbc, times(1)).update(eq(DELETE_AD_HOC), eq(mentorId));

    verify(jdbc, times(2)).update(eq(INSERT_AD_HOC), eq(mentorId), anyInt(), anyInt());
  }

  @Test
  @DisplayName("Given mentee section, when updating, then text and long-term fields are updated")
  void testUpdateMenteeSectionUpdatesTextAndLongTermFields() {
    menteeSecRepo.updateMenteeSection(menteeSection, mentorId);

    verify(jdbc)
        .update(
            eq(UPDATE_MENTEE_SECTION),
            eq("Ideal mentee description UPDATED"),
            eq("Additional info UPDATED"),
            eq(2),
            eq(8),
            eq(mentorId));
  }

  @Test
  @DisplayName(
      "Given mentee section with ad-hoc, when updating, then old ad-hoc availability is deleted")
  void testUpdateMenteeSectionDeletesOldAdHocAvailability() {
    menteeSecRepo.updateMenteeSection(menteeSection, mentorId);

    verify(jdbc).update(eq(DELETE_AD_HOC), eq(mentorId));
  }

  @Test
  @DisplayName(
      "Given mentee section with ad-hoc availability, when updating, then all months are inserted")
  void testUpdateMenteeSectionInsertsAllAdHocAvailability() {
    menteeSecRepo.updateMenteeSection(menteeSection, mentorId);

    verify(jdbc).update(eq(INSERT_AD_HOC), eq(mentorId), eq(Month.JANUARY.getValue()), eq(2));

    verify(jdbc).update(eq(INSERT_AD_HOC), eq(mentorId), eq(Month.FEBRUARY.getValue()), eq(3));
  }

  @Test
  @DisplayName(
      "Given mentee section without long-term, when updating, then long-term fields are null")
  void testUpdateMenteeSectionWithNoLongTerm() {
    MenteeSection adHocOnlySection =
        new MenteeSection(
            "Ideal mentee",
            "Additional",
            null, // No long-term
            List.of(new MentorMonthAvailability(Month.APRIL, 1)));

    menteeSecRepo.updateMenteeSection(adHocOnlySection, mentorId);

    verify(jdbc)
        .update(
            eq(UPDATE_MENTEE_SECTION),
            eq("Ideal mentee"),
            eq("Additional"),
            isNull(),
            isNull(),
            eq(mentorId));

    verify(jdbc, times(1))
        .update(eq(INSERT_AD_HOC), eq(mentorId), eq(Month.APRIL.getValue()), eq(1));
  }

  @Test
  @DisplayName(
      "Given mentee section with empty ad-hoc availability, when updating, then no ad-hoc records"
          + " are inserted")
  void testUpdateMenteeSectionWithEmptyAdHocAvailability() {
    MenteeSection longTermOnlySection =
        new MenteeSection(
            "Ideal mentee",
            "Additional",
            new LongTermMentorship(1, 4),
            List.of() // Empty ad-hoc availability
            );

    menteeSecRepo.updateMenteeSection(longTermOnlySection, mentorId);

    verify(jdbc).update(eq(DELETE_AD_HOC), eq(mentorId));
    verify(jdbc, never()).update(eq(INSERT_AD_HOC), anyLong(), anyInt(), anyInt());
  }

  @Test
  @DisplayName(
      "Given mentee section with null ad-hoc availability, when updating, then no ad-hoc records"
          + " are inserted")
  void testUpdateMenteeSectionWithNullAdHocAvailability() {
    MenteeSection longTermOnlySection =
        new MenteeSection(
            "Ideal mentee", "Additional", new LongTermMentorship(1, 4), null // Null ad-hoc
            );

    menteeSecRepo.updateMenteeSection(longTermOnlySection, mentorId);

    verify(jdbc).update(eq(DELETE_AD_HOC), eq(mentorId));
    verify(jdbc, never()).update(eq(INSERT_AD_HOC), anyLong(), anyInt(), anyInt());
  }

  @Test
  @DisplayName("Given mentee section, when updating, then all required SQL statements are executed")
  void testUpdateMenteeSectionVerifiesAllSqlStatements() {
    menteeSecRepo.updateMenteeSection(menteeSection, mentorId);

    verify(jdbc)
        .update(eq(UPDATE_MENTEE_SECTION), anyString(), anyString(), any(), any(), anyLong());
    verify(jdbc).update(eq(DELETE_AD_HOC), anyLong());
    verify(jdbc, atLeastOnce()).update(eq(INSERT_AD_HOC), anyLong(), anyInt(), anyInt());
  }
}
