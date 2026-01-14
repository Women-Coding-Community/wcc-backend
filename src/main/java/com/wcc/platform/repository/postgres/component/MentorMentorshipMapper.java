package com.wcc.platform.repository.postgres.component;

import com.wcc.platform.domain.cms.pages.mentorship.MenteeSection;
import com.wcc.platform.domain.cms.pages.mentorship.MentorMonthAvailability;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Utility class for inserting mentorship-related data. Maps database rows to Mentor domain objects.
 */
@Slf4j
@Component
public final class MentorMentorshipMapper {
  private static final String INSERT_MENTOR_MENTEE =
      "INSERT INTO mentor_mentee_section (mentor_id, ideal_mentee, additional) VALUES (?, ?, ?)";
  private static final String INSERT_AVAILABILITY =
      "INSERT INTO mentor_availability (mentor_id, month_num, hours) VALUES (?, ?, ?)";
  private static final String INSERT_MENTOR_TYPES =
      "INSERT INTO mentor_mentorship_types (mentor_id, mentorship_type) VALUES (?, ?)";

  @Setter private static JdbcTemplate jdbc;

  private MentorMentorshipMapper() {
    // Utility class - private constructor
  }

  /** Inserts the mentee section details for the mentor. */
  public static void insertMenteeSection(final MenteeSection menteeSec, final Long memberId) {
    if (jdbc == null) {
      log.error("JdbcTemplate not set in MentorMentorshipMapper");
      throw new IllegalStateException("JdbcTemplate not initialized");
    }
    jdbc.update(INSERT_MENTOR_MENTEE, memberId, menteeSec.idealMentee(), menteeSec.additional());
    insertAvailability(menteeSec, memberId);
    insertMentorshipTypes(menteeSec, memberId);
  }

  /* default */ private static void insertAvailability(
      final MenteeSection ms, final Long memberId) {
    for (final MentorMonthAvailability a : ms.availability()) {
      jdbc.update(INSERT_AVAILABILITY, memberId, a.month().getValue(), a.hours());
    }
  }

  /* default */ private static void insertMentorshipTypes(
      final MenteeSection ms, final Long memberId) {
    for (final MentorshipType mt : ms.mentorshipType()) {
      jdbc.update(INSERT_MENTOR_TYPES, memberId, mt.getMentorshipTypeId());
    }
  }
}
