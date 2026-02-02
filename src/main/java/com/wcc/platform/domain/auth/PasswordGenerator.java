package com.wcc.platform.domain.auth;

import java.security.SecureRandom;

public class PasswordGenerator {
  private static final String CHAR_LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
  private static final String CHAR_UPPERCASE = CHAR_LOWERCASE.toUpperCase();
  private static final String PASSWORD_ALLOW =
      CHAR_LOWERCASE + CHAR_UPPERCASE + DIGIT + SPECIAL_CHARS;
  private static final String DIGIT = "0123456789";
  private static final String SPECIAL_CHARS = "!@#$%&*()_+-=[]|,./?><";
  private static final SecureRandom random = new SecureRandom();

  public static String generateRandomPassword(int length) {
    if (length < 8) {
      throw new IllegalArgumentException("Password length must be at least 8");
    }

    StringBuilder password = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      int randomIndex = random.nextInt(PASSWORD_ALLOW.length());
      password.append(PASSWORD_ALLOW.charAt(randomIndex));
    }

    return password.toString();
  }
}
