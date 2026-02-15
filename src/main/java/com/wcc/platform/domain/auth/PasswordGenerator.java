package com.wcc.platform.domain.auth;

import java.security.SecureRandom;
import java.util.Locale;

public final class PasswordGenerator {
  private static final String CHAR_LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
  private static final String CHAR_UPPERCASE = CHAR_LOWERCASE.toUpperCase(Locale.ENGLISH);
  private static final String DIGIT = "0123456789";
  private static final String SPECIAL_CHARS = "!@#$%&*()_+-=[]|,./?><";
  private static final String PASSWORD_ALLOW =
      CHAR_LOWERCASE + CHAR_UPPERCASE + DIGIT + SPECIAL_CHARS;
  private static final SecureRandom RANDOM = new SecureRandom();
  private static final Integer PASSWORD_LENGTH = 12;

  private PasswordGenerator() {}

  public static String generateRandomPassword() {
    final StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
    for (int i = 0; i < PASSWORD_LENGTH; i++) {
      final int randomIndex = RANDOM.nextInt(PASSWORD_ALLOW.length());
      password.append(PASSWORD_ALLOW.charAt(randomIndex));
    }

    return password.toString();
  }
}
