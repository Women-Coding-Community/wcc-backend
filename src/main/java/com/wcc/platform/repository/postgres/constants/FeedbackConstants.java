package com.wcc.platform.repository.postgres.constants;

/** Constants related to Feedback entity. */
public final class FeedbackConstants {

  public static final String TABLE = "feedback";
  public static final String COLUMN_ID = "id";
  public static final String COLUMN_REVIEWER_ID = "reviewer_id";
  public static final String COLUMN_REVIEWEE_ID = "reviewee_id";
  public static final String COLUMN_MENTORSHIP_CYCLE_ID = "mentorship_cycle_id";
  public static final String COLUMN_FEEDBACK_TYPE_ID = "feedback_type_id";
  public static final String COLUMN_RATING = "rating";
  public static final String COLUMN_FEEDBACK_TEXT = "feedback_text";
  public static final String COLUMN_YEAR = "feedback_year";
  public static final String COLUMN_IS_ANONYMOUS = "is_anonymous";
  public static final String COLUMN_IS_APPROVED = "is_approved";
  public static final String COLUMN_CREATED_AT = "created_at";
  public static final String COLUMN_UPDATED_AT = "updated_at";

  private FeedbackConstants() {}
}
