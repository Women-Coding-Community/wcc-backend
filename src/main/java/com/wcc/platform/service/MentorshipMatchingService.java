package com.wcc.platform.service;

import com.wcc.platform.domain.exceptions.ApplicationNotFoundException;
import com.wcc.platform.domain.exceptions.MentorCapacityExceededException;
import com.wcc.platform.domain.platform.mentorship.ApplicationStatus;
import com.wcc.platform.domain.platform.mentorship.MatchStatus;
import com.wcc.platform.domain.platform.mentorship.MenteeApplication;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleEntity;
import com.wcc.platform.domain.platform.mentorship.MentorshipMatch;
import com.wcc.platform.repository.MenteeApplicationRepository;
import com.wcc.platform.repository.MentorshipCycleRepository;
import com.wcc.platform.repository.MentorshipMatchRepository;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing confirmed mentorship matches.
 * Handles match creation, lifecycle management, and cleanup.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipMatchingService {

    private final MentorshipMatchRepository matchRepository;
    private final MenteeApplicationRepository applicationRepository;
    private final MentorshipCycleRepository cycleRepository;

    /**
     * Confirm a match from an accepted application.
     * This is typically done by mentorship team after mentor acceptance.
     *
     * @param applicationId the accepted application ID
     * @return created match
     * @throws ApplicationNotFoundException if application not found
     * @throws IllegalStateException if application not in accepted state
     * @throws MentorCapacityExceededException if mentor at capacity
     */
    @Transactional
    public MentorshipMatch confirmMatch(final Long applicationId) {
        final MenteeApplication application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new ApplicationNotFoundException(applicationId));

        validateApplicationCanBeMatched(application);
        checkMentorCapacity(application.getMentorId(), application.getCycleId());
        checkMenteeNotAlreadyMatched(application.getMenteeId(), application.getCycleId());

        final MentorshipCycleEntity cycle = cycleRepository.findById(application.getCycleId())
            .orElseThrow(() -> new IllegalArgumentException(
                "Cycle not found: " + application.getCycleId()));

        final MentorshipMatch match = MentorshipMatch.builder()
            .mentorId(application.getMentorId())
            .menteeId(application.getMenteeId())
            .cycleId(application.getCycleId())
            .applicationId(applicationId)
            .status(MatchStatus.ACTIVE)
            .startDate(LocalDate.now())
            .expectedEndDate(cycle.getCycleEndDate())
            .sessionFrequency("Weekly") // Default, can be customized
            .totalSessions(0)
            .createdAt(ZonedDateTime.now())
            .updatedAt(ZonedDateTime.now())
            .build();

        final MentorshipMatch created = matchRepository.create(match);

        // Update application status to MATCHED
        applicationRepository.updateStatus(
            applicationId,
            ApplicationStatus.MATCHED,
            "Match confirmed by mentorship team"
        );

        // Reject all other pending applications for this mentee in this cycle
        rejectOtherApplications(application.getMenteeId(), application.getCycleId(), applicationId);

        log.info("Match confirmed: mentor {} with mentee {} for cycle {}",
            application.getMentorId(), application.getMenteeId(), application.getCycleId());

        return created;
    }

    /**
     * Complete a mentorship match when the cycle ends or goals are achieved.
     *
     * @param matchId the match ID
     * @param notes completion notes
     * @return updated match
     * @throws IllegalArgumentException if match not found
     */
    @Transactional
    public MentorshipMatch completeMatch(final Long matchId, final String notes) {
        final MentorshipMatch match = getMatchOrThrow(matchId);

        validateMatchCanBeCompleted(match);

        final MentorshipMatch updated = MentorshipMatch.builder()
            .matchId(match.getMatchId())
            .mentorId(match.getMentorId())
            .menteeId(match.getMenteeId())
            .cycleId(match.getCycleId())
            .applicationId(match.getApplicationId())
            .status(MatchStatus.COMPLETED)
            .startDate(match.getStartDate())
            .endDate(LocalDate.now())
            .expectedEndDate(match.getExpectedEndDate())
            .sessionFrequency(match.getSessionFrequency())
            .totalSessions(match.getTotalSessions())
            .createdAt(match.getCreatedAt())
            .updatedAt(ZonedDateTime.now())
            .build();

        final MentorshipMatch result = matchRepository.update(matchId, updated);

        log.info("Match {} completed between mentor {} and mentee {}",
            matchId, match.getMentorId(), match.getMenteeId());

        return result;
    }

    /**
     * Cancel a mentorship match.
     *
     * @param matchId the match ID
     * @param reason cancellation reason
     * @param cancelledBy who cancelled (mentor/mentee/admin)
     * @return updated match
     * @throws IllegalArgumentException if match not found
     */
    @Transactional
    public MentorshipMatch cancelMatch(
            final Long matchId,
            final String reason,
            final String cancelledBy) {

        final MentorshipMatch match = getMatchOrThrow(matchId);

        validateMatchCanBeCancelled(match);

        final MentorshipMatch updated = MentorshipMatch.builder()
            .matchId(match.getMatchId())
            .mentorId(match.getMentorId())
            .menteeId(match.getMenteeId())
            .cycleId(match.getCycleId())
            .applicationId(match.getApplicationId())
            .status(MatchStatus.CANCELLED)
            .startDate(match.getStartDate())
            .endDate(LocalDate.now())
            .expectedEndDate(match.getExpectedEndDate())
            .sessionFrequency(match.getSessionFrequency())
            .totalSessions(match.getTotalSessions())
            .cancellationReason(reason)
            .cancelledBy(cancelledBy)
            .cancelledAt(ZonedDateTime.now())
            .createdAt(match.getCreatedAt())
            .updatedAt(ZonedDateTime.now())
            .build();

        final MentorshipMatch result = matchRepository.update(matchId, updated);

        log.info("Match {} cancelled by {} - reason: {}",
            matchId, cancelledBy, reason);

        return result;
    }

    /**
     * Get all active matches for a mentor.
     *
     * @param mentorId the mentor ID
     * @return list of active matches
     */
    public List<MentorshipMatch> getActiveMentorMatches(final Long mentorId) {
        return matchRepository.findActiveMenteesByMentor(mentorId);
    }

    /**
     * Get the active mentor for a mentee (should be only one).
     *
     * @param menteeId the mentee ID
     * @return active match if exists
     */
    public MentorshipMatch getActiveMenteeMatch(final Long menteeId) {
        return matchRepository.findActiveMentorByMentee(menteeId).orElse(null);
    }

    /**
     * Get all matches for a cycle.
     *
     * @param cycleId the cycle ID
     * @return list of matches
     */
    public List<MentorshipMatch> getCycleMatches(final Long cycleId) {
        return matchRepository.findByCycle(cycleId);
    }

    /**
     * Increment session count for a match.
     *
     * @param matchId the match ID
     * @return updated match
     */
    @Transactional
    public MentorshipMatch incrementSessionCount(final Long matchId) {
        final MentorshipMatch match = getMatchOrThrow(matchId);

        if (match.getStatus() != MatchStatus.ACTIVE) {
            throw new IllegalStateException("Can only track sessions for active matches");
        }

        final MentorshipMatch updated = MentorshipMatch.builder()
            .matchId(match.getMatchId())
            .mentorId(match.getMentorId())
            .menteeId(match.getMenteeId())
            .cycleId(match.getCycleId())
            .applicationId(match.getApplicationId())
            .status(match.getStatus())
            .startDate(match.getStartDate())
            .endDate(match.getEndDate())
            .expectedEndDate(match.getExpectedEndDate())
            .sessionFrequency(match.getSessionFrequency())
            .totalSessions(match.getTotalSessions() + 1)
            .cancellationReason(match.getCancellationReason())
            .cancelledBy(match.getCancelledBy())
            .cancelledAt(match.getCancelledAt())
            .createdAt(match.getCreatedAt())
            .updatedAt(ZonedDateTime.now())
            .build();

        return matchRepository.update(matchId, updated);
    }

    // Private helper methods

    private MentorshipMatch getMatchOrThrow(final Long matchId) {
        return matchRepository.findById(matchId)
            .orElseThrow(() -> new IllegalArgumentException("Match not found: " + matchId));
    }

    private void validateApplicationCanBeMatched(final MenteeApplication application) {
        if (application.getStatus() != ApplicationStatus.MENTOR_ACCEPTED) {
            throw new IllegalStateException(
                "Can only confirm matches from MENTOR_ACCEPTED applications, current status: "
                    + application.getStatus()
            );
        }
    }

    private void validateMatchCanBeCompleted(final MentorshipMatch match) {
        if (match.getStatus() != MatchStatus.ACTIVE) {
            throw new IllegalStateException(
                "Can only complete ACTIVE matches, current status: " + match.getStatus()
            );
        }
    }

    private void validateMatchCanBeCancelled(final MentorshipMatch match) {
        if (match.getStatus() == MatchStatus.COMPLETED || match.getStatus() == MatchStatus.CANCELLED) {
            throw new IllegalStateException(
                "Cannot cancel match in terminal state: " + match.getStatus()
            );
        }
    }

    private void checkMentorCapacity(final Long mentorId, final Long cycleId) {
        final MentorshipCycleEntity cycle = cycleRepository.findById(cycleId)
            .orElseThrow(() -> new IllegalArgumentException("Cycle not found: " + cycleId));

        final int currentMentees = matchRepository.countActiveMenteesByMentorAndCycle(
            mentorId, cycleId
        );

        if (currentMentees >= cycle.getMaxMenteesPerMentor()) {
            throw new MentorCapacityExceededException(
                String.format("Mentor %d has reached maximum capacity (%d) for cycle %d",
                    mentorId, cycle.getMaxMenteesPerMentor(), cycleId)
            );
        }
    }

    private void checkMenteeNotAlreadyMatched(final Long menteeId, final Long cycleId) {
        if (matchRepository.isMenteeMatchedInCycle(menteeId, cycleId)) {
            throw new IllegalStateException(
                String.format("Mentee %d is already matched in cycle %d", menteeId, cycleId)
            );
        }
    }

    private void rejectOtherApplications(
            final Long menteeId,
            final Long cycleId,
            final Long acceptedApplicationId) {

        final List<MenteeApplication> otherApplications =
            applicationRepository.findByMenteeAndCycleOrderByPriority(menteeId, cycleId);

        otherApplications.stream()
            .filter(app -> !app.getApplicationId().equals(acceptedApplicationId))
            .filter(app -> app.getStatus().isPendingMentorAction()
                || app.getStatus() == ApplicationStatus.MENTOR_ACCEPTED)
            .forEach(app -> {
                applicationRepository.updateStatus(
                    app.getApplicationId(),
                    ApplicationStatus.REJECTED,
                    "Mentee matched with another mentor"
                );
                log.info("Rejected application {} as mentee {} was matched with another mentor",
                    app.getApplicationId(), menteeId);
            });
    }
}
