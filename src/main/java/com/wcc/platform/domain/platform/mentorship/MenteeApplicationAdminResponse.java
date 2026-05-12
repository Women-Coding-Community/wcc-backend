package com.wcc.platform.domain.platform.mentorship;

import java.time.ZonedDateTime;

/**
 * Response DTO for admin view of mentee applications. Includes mentee profile, status, and
 * rejection reason.
 */
public record MenteeApplicationAdminResponse(
    Long applicationId,
    Long menteeId,
    Mentee mentee,
    Long mentorId,
    ApplicationStatus status,
    String rejectionReason,
    ZonedDateTime appliedAt) {}
