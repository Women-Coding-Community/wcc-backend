package com.wcc.platform.repository.postgres.constants;

/** Constants related to Mentor entity. */
@SuppressWarnings("PMD.DataClass")
public final class MentorConstants {

  // MENTOR table
  public static final String COLUMN_MENTOR_ID = "mentor_id";
  public static final String COLUMN_PROFILE_STATUS = "profile_status";
  public static final String COLUMN_BIO = "bio";
  public static final String COLUMN_SPOKEN_LANG = "spoken_languages";

  // MENTOR_MENTEE_SECTION table
  public static final String COLUMN_IDEAL_MENTEE = "ideal_mentee";
  public static final String COLUMN_ADDITIONAL = "additional";

  // OTHERS
  public static final String COL_MTRSHIP_TYPE_ID = "mentorship_type_id";
  public static final String COLUMN_MONTH = "month_num";
  public static final String COLUMN_HOURS = "hours";
  public static final String COLUMN_YEARS_EXP = "years_experience";

  private MentorConstants() {}
}
