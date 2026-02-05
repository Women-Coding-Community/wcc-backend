package com.wcc.platform.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.Year;
import org.apache.commons.lang3.StringUtils;

public class YearDeserializer extends JsonDeserializer<Year> {

  @Override
  public Year deserialize(final JsonParser jsonParser, final DeserializationContext context)
      throws IOException {
    if (jsonParser.currentToken() == JsonToken.VALUE_NULL) {
      return null;
    }

    if (jsonParser.currentToken() == JsonToken.VALUE_NUMBER_INT) {
      return Year.of(jsonParser.getIntValue());
    }

    if (jsonParser.currentToken() == JsonToken.VALUE_STRING) {
      final String year = jsonParser.getText();
      if (!StringUtils.isBlank(year)) {
        return Year.parse(year.trim());
      }
      return null;
    }

    throw new IllegalArgumentException(
        "Cannot deserialize Year from token: " + jsonParser.currentToken());
  }
}
