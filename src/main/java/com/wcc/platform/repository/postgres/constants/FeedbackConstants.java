package com.wcc.platform.repository.postgres.constants;

/** Constants related to Feedback entity. */
@SuppressWarnings("PMD.DataClass")
public final class FeedbackConstants {

  public static final String TABLE = "feedback";
  public static final String COL_ID = "id";
  public static final String COL_REVIEWER_ID = "reviewer_id";
  public static final String COL_REVIEWEE_ID = "reviewee_id";
  public static final String COL_REVIEWER_NAME = "reviewer_name";
  public static final String COL_REVIEWEE_NAME = "reviewee_name";
  public static final String COL_MS_CYCLE_ID = "mentorship_cycle_id";
  public static final String COL_FB_TYPE_ID = "feedback_type_id";
  public static final String COL_RATING = "rating";
  public static final String COL_FEEDBACK_TEXT = "feedback_text";
  public static final String COL_YEAR = "feedback_year";
  public static final String COL_IS_ANONYMOUS = "is_anonymous";
  public static final String COL_IS_APPROVED = "is_approved";
  public static final String COL_CREATED_AT = "created_at";
  public static final String COL_UPDATED_AT = "updated_at";

  private FeedbackConstants() {}
}
