package com.wcc.platform.repository.postgres.constants;

/** Constants related to Mentor entity. */
@SuppressWarnings("PMD.DataClass")
public final class MentorConstants {

  // MENTOR table
  public static final String COLUMN_MENTOR_ID = "mentor_id";
  public static final String COLUMN_PROFILE_STATUS = "profile_status";
  public static final String COLUMN_BIO = "bio";
  public static final String COLUMN_SPOKEN_LANG = "spoken_languages";
  public static final String COL_CALENDLY_LINK = "calendly_link";
  public static final String COL_ACCEPT_MALE = "accept_male_mentee";
  public static final String COL_ACCEPT_PROMO = "accept_promote_social_media";

  // MENTOR_MENTEE_SECTION table
  public static final String COLUMN_IDEAL_MENTEE = "ideal_mentee";
  public static final String COLUMN_ADDITIONAL = "additional";
  public static final String COLUMN_LT_NUM_MENTEE = "long_term_num_mentee";
  public static final String COLUMN_LT_HOURS = "long_term_hours";

  // OTHERS
  public static final String COL_MENTORSHIP_TYPE = "mentorship_type";
  public static final String COLUMN_MONTH = "month_num";
  public static final String COLUMN_HOURS = "hours";

  private MentorConstants() {}
}
