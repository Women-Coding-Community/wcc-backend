package com.wcc.platform.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.Year;

public class YearDeserializer extends JsonDeserializer<Year> {

  @Override
  public Year deserialize(final JsonParser p, final DeserializationContext ctxt)
      throws IOException {
    // Handle null values
    if (p.currentToken() == JsonToken.VALUE_NULL) {
      return null;
    }

    // Handle numeric values (e.g., 2026)
    if (p.currentToken() == JsonToken.VALUE_NUMBER_INT) {
      return Year.of(p.getIntValue());
    }

    // Handle string values (e.g., "2026")
    if (p.currentToken() == JsonToken.VALUE_STRING) {
      String text = p.getText().trim();
      if (text.isEmpty()) {
        return null;
      }
      return Year.parse(text);
    }

    throw new IllegalArgumentException(
        "Cannot deserialize Year from token: " + p.currentToken());
  }
}
