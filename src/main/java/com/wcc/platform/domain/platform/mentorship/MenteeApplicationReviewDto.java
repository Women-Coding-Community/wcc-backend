package com.wcc.platform.domain.platform.mentorship;

/**
 * DTO for admin review of a pending mentee application.
 *
 * <p>Combines application details with mentee profile information to support the admin review
 * workflow for priority-1 PENDING applications.
 *
 * @param applicationId application ID
 * @param menteeId mentee ID
 * @param fullName mentee full name
 * @param position current job position
 * @param yearsExperience years of experience (may be null)
 */
public record MenteeApplicationReviewDto(
    Long applicationId,
    Long menteeId,
    String fullName,
    String position,
    Integer yearsExperience,
    String linkedinUrl,
    String slackDisplayName,
    String email,
    String mentorshipGoal) {}
