package com.wcc.platform.repository.postgres.mentorship;

import java.sql.ResultSet;
import java.sql.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.wcc.platform.domain.platform.mentorship.MatchStatus;
import com.wcc.platform.domain.platform.mentorship.MentorshipMatch;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.JdbcTemplate;

class PostgresMentorshipMatchRepositoryTest {

    private JdbcTemplate jdbc;
    private PostgresMentorshipMatchRepository repository;

    @BeforeEach
    void setup() {
        jdbc = mock(JdbcTemplate.class);
        repository = spy(new PostgresMentorshipMatchRepository(jdbc));
    }

    @Test
    void testUpdateCompletionNotes() {
        Long matchId = 100L;
        String notes = "All planned sessions completed";

        MentorshipMatch match = MentorshipMatch.builder()
            .matchId(matchId)
            .mentorId(20L)
            .menteeId(10L)
            .cycleId(5L)
            .status(MatchStatus.COMPLETED)
            .totalSessions(5)
            .completionNotes(notes)
            .build();

        doReturn(Optional.of(match))
            .when(repository).findById(matchId);

        when(jdbc.update(any(String.class), any(Object[].class)))
            .thenReturn(1);

        repository.update(matchId, match);

        ArgumentCaptor<String> sqlCaptor =
            ArgumentCaptor.forClass(String.class);

        ArgumentCaptor<Object[]> paramsCaptor =
            ArgumentCaptor.forClass(Object[].class);

        verify(jdbc).update(sqlCaptor.capture(), paramsCaptor.capture());

        assertTrue(sqlCaptor.getValue().contains("completion_notes"));

        Object[] params = paramsCaptor.getValue();

        assertEquals(notes, params[8]);
        assertEquals(matchId, params[9]);
    }

    @Test
    void testFindByIdCompletionNotes() throws Exception {
        Long matchId = 100L;
        String notes = "All planned sessions completed";

        ResultSet rs = mock(ResultSet.class);

        when(rs.next()).thenReturn(true);
        when(rs.getLong("match_id")).thenReturn(matchId);
        when(rs.getLong("mentor_id")).thenReturn(20L);
        when(rs.getLong("mentee_id")).thenReturn(10L);
        when(rs.getLong("cycle_id")).thenReturn(5L);
        when(rs.getString("match_status")).thenReturn("completed");
        when(rs.getDate("start_date"))
            .thenReturn(Date.valueOf("2026-01-01"));
        when(rs.getInt("total_sessions")).thenReturn(5);
        when(rs.getString("completion_notes")).thenReturn(notes);

        when(jdbc.query(
            any(String.class),
            any(org.springframework.jdbc.core.ResultSetExtractor.class),
            eq(matchId)))
            .thenAnswer(invocation -> {
                org.springframework.jdbc.core.ResultSetExtractor<?> extractor =
                    invocation.getArgument(1);
                return extractor.extractData(rs);
            });

        Optional<MentorshipMatch> result = repository.findById(matchId);

        assertTrue(result.isPresent());
        assertEquals(notes, result.get().getCompletionNotes());
    }
}