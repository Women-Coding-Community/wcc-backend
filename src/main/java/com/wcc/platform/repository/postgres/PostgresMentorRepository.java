package com.wcc.platform.repository.postgres;

import static com.wcc.platform.repository.postgres.constants.MentorConstants.COLUMN_MENTOR_ID;

import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.repository.MentorRepository;
import com.wcc.platform.repository.postgres.component.MemberMapper;
import com.wcc.platform.repository.postgres.component.MentorMapper;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * A repository implementation for managing Mentor entities in a PostgreSQL database. Provides data
 * access methods for Mentor objects, leveraging JdbcTemplate for interaction with the database.
 *
 * <p>This class relies on SQL queries and mappers to interact with the database, ensuring effective
 * and type-safe ORM-like behavior for Mentor-specific data operations.
 *
 * <p>The repository is marked as a Spring `@Repository` to enable automatic exception translation
 * and dependency injection. It uses `@RequiredArgsConstructor` to generate constructors for
 * required dependencies.
 */
@Repository
@RequiredArgsConstructor
public class PostgresMentorRepository implements MentorRepository {

  /* default */ static final String UPDATE_MENTOR_SQL =
      "UPDATE mentors SET "
          + "profile_status = ?, "
          + "bio = ?, "
          + "years_experience = ?, "
          + "spoken_languages = ?, "
          + "is_available = ? "
          + "WHERE mentor_id = ?";
  private static final String SQL_INSERT_MENTOR =
      "INSERT INTO mentors (mentor_id, profile_status, bio, years_experience, "
          + " spoken_languages, is_available) VALUES (?, ?, ?, ?, ?, ?)";
  private static final String SQL_GET_BY_ID = "SELECT * FROM mentors WHERE mentor_id = ?";
  private static final String SQL_DELETE_BY_ID = "DELETE FROM mentors WHERE mentor_id = ?";
  private static final String SQL_GET_BY_EMAIL =
      "SELECT mentors.* FROM mentors LEFT JOIN members ON mentors.mentor_id = members.id "
          + "WHERE members.email = ?";
  private static final String SQL_GET_ALL = "SELECT * FROM mentors";
  private static final String SQL_FIND_ID_BY_EMAIL =
      "SELECT mentors.mentor_id FROM mentors LEFT JOIN members ON mentors.mentor_id = members.id"
          + " WHERE members.email = ?";

  private final JdbcTemplate jdbc;
  private final MentorMapper mentorMapper;
  private final MemberMapper memberMapper;

  @Override
  public Optional<Mentor> findByEmail(final String email) {
    return jdbc.query(
        SQL_GET_BY_EMAIL,
        rs -> {
          if (rs.next()) {
            return Optional.of(mentorMapper.mapRowToMentor(rs));
          }
          return Optional.empty();
        },
        email);
  }

  @Override
  public List<Mentor> getAll() {
    return jdbc.query(SQL_GET_ALL, (rs, rowNum) -> mentorMapper.mapRowToMentor(rs));
  }

  @Override
  public Long findIdByEmail(final String email) {
    return jdbc.query(
        SQL_FIND_ID_BY_EMAIL,
        rs -> {
          if (rs.next()) {
            return rs.getLong(COLUMN_MENTOR_ID);
          }
          return null;
        },
        email);
  }

  @Override
  @Transactional
  public Mentor create(final Mentor mentor) {
    final Long memberId = memberMapper.addMember(mentor);
    addMentor(mentor, memberId);
    final var mentorAdded = findById(memberId);
    return mentorAdded.orElse(null);
  }

  @Override
  @Transactional
  public Mentor update(final Long mentorId, final Mentor mentor) {
    memberMapper.updateMember(mentor, mentorId);
    updateMentor(mentor, mentorId);
    return findById(mentorId).orElse(null);
  }

  @Override
  public Optional<Mentor> findById(final Long mentorId) {
    return jdbc.query(
        SQL_GET_BY_ID,
        rs -> {
          if (rs.next()) {
            return Optional.of(mentorMapper.mapRowToMentor(rs));
          }
          return Optional.empty();
        },
        mentorId);
  }

  @Override
  public void deleteById(final Long mentorId) {
    jdbc.update(SQL_DELETE_BY_ID, mentorId);
  }

  /** Inserts a new Mentor into the database, including related skills and mentee section. */
  private void addMentor(final Mentor mentor, final Long memberId) {
    insertMentor(mentor, memberId);
    mentorMapper.addMentor(mentor, memberId);
  }

  /** Inserts mentor-specific details into the mentor. */
  private void insertMentor(final Mentor mentor, final Long memberId) {
    final var profileStatus = mentor.getProfileStatus();
    final var skills = mentor.getSkills();
    jdbc.update(
        SQL_INSERT_MENTOR,
        memberId,
        profileStatus.getStatusId(),
        mentor.getBio(),
        skills.yearsExperience(),
        String.join(",", mentor.getSpokenLanguages()),
        true);
  }

  /**
   * Updates an existing Mentor in the database, including related skills and mentee section. An
   * existing mentee section will be deleted and a new one inserted.
   */
  private void updateMentor(final Mentor mentor, final Long mentorId) {
    updateMentorDetails(mentor, mentorId);
    mentorMapper.updateMentor(mentor, mentorId);
  }

  private void updateMentorDetails(final Mentor mentor, final Long mentorId) {
    final var skills = mentor.getSkills();
    jdbc.update(
        UPDATE_MENTOR_SQL,
        mentor.getProfileStatus().getStatusId(),
        mentor.getBio(),
        skills.yearsExperience(),
        String.join(",", mentor.getSpokenLanguages()),
        true,
        mentorId);
  }
}
