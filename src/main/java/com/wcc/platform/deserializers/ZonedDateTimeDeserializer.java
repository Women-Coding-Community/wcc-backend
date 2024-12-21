package com.wcc.platform.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/** Custom Deserialize for ZonedDateTime. */
public class ZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {
  private final DateTimeFormatter formatter;

  public ZonedDateTimeDeserializer(final DateTimeFormatter formatter) {
    super();
    this.formatter = formatter;
  }

  @Override
  public ZonedDateTime deserialize(final JsonParser parser, DeserializationContext ctxt)
      throws IOException {
    return ZonedDateTime.parse(parser.getText(), formatter);
  }
}
