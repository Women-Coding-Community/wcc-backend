package com.wcc.platform.utils;

/** Utility class for JSON-related operations. */
public final class JsonUtil {
  private static final char DOUBLE_QUOTE = '"';
  private static final char SINGLE_QUOTE = '\'';
  private static final String BACKSLASH = "\\\\";
  private static final String BACKSLASH_JSON = "\\";
  private static final String ESCAPE = "\\\"";
  private static final String ESCAPE_JSON = "\"";

  private JsonUtil() {
    // Utility class, no instances.
  }

  /**
   * Normalizes a JSON-formatted string by trimming leading and trailing spaces. If the string is
   * enclosed with single or double quotes, the method removes the quotes and processes escaped
   * characters to their JSON-unescaped equivalents.
   *
   * @param value the JSON string to be normalized; can be null
   * @return the normalized JSON string; returns null if the input is null
   */
  public static String normalizeJson(final String value) {
    if (value == null) {
      return null;
    }

    String cleaned = value.trim();
    final var minJsonLength = 2;
    if (cleaned.length() < minJsonLength) {
      return cleaned;
    }

    final char first = cleaned.charAt(0);
    final char last = cleaned.charAt(cleaned.length() - 1);

    if ((first == DOUBLE_QUOTE && last == DOUBLE_QUOTE)
        || (first == SINGLE_QUOTE && last == SINGLE_QUOTE)) {
      cleaned =
          cleaned
              .substring(1, cleaned.length() - 1)
              .replace(ESCAPE, ESCAPE_JSON)
              .replace(BACKSLASH, BACKSLASH_JSON);
    }

    return cleaned;
  }
}
