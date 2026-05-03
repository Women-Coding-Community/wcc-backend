package com.wcc.platform.domain.platform.mentorship;

import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.domain.platform.SocialNetworkType;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Response DTO for mentee applications with enriched mentee information. Includes mentee name,
 * LinkedIn profile link, and bio for display purposes.
 */
public record MenteeApplicationResponse(
    Long applicationId,
    Long menteeId,
    Long mentorId,
    Long cycleId,
    Integer priorityOrder,
    ApplicationStatus status,
    String applicationMessage,
    String whyMentor,
    ZonedDateTime appliedAt,
    ZonedDateTime reviewedAt,
    ZonedDateTime matchedAt,
    String mentorResponse,
    ZonedDateTime createdAt,
    ZonedDateTime updatedAt,
    boolean reviewed,
    boolean matched,
    long daysSinceApplied,
    String menteeName,
    String menteeLinkedIn,
    String menteeBio) {

  /**
   * Creates an enriched response from a MenteeApplication and mentee details.
   *
   * @param application the base application
   * @param menteeName the mentee's full name
   * @param networks the mentee's social networks (to extract LinkedIn)
   * @param bio the mentee's bio
   * @return enriched response with mentee information
   */
  public static MenteeApplicationResponse from(
      final MenteeApplication application,
      final String menteeName,
      final List<SocialNetwork> networks,
      final String bio) {

    final String linkedIn = extractLinkedIn(networks);

    return new MenteeApplicationResponse(
        application.getApplicationId(),
        application.getMenteeId(),
        application.getMentorId(),
        application.getCycleId(),
        application.getPriorityOrder(),
        application.getStatus(),
        application.getApplicationMessage(),
        application.getWhyMentor(),
        application.getAppliedAt(),
        application.getReviewedAt(),
        application.getMatchedAt(),
        application.getMentorResponse(),
        application.getCreatedAt(),
        application.getUpdatedAt(),
        application.isReviewed(),
        application.isMatched(),
        application.getDaysSinceApplied(),
        menteeName,
        linkedIn,
        bio);
  }

  /**
   * Creates a response from a MenteeApplication without enrichment. Used when mentee data is not
   * available.
   *
   * @param application the base application
   * @return response without enriched mentee information
   */
  public static MenteeApplicationResponse from(final MenteeApplication application) {
    return from(application, null, List.of(), null);
  }

  private static String extractLinkedIn(final List<SocialNetwork> networks) {
    if (networks == null) {
      return null;
    }
    return networks.stream()
        .filter(n -> n.type() == SocialNetworkType.LINKEDIN)
        .map(SocialNetwork::link)
        .findFirst()
        .orElse(null);
  }
}