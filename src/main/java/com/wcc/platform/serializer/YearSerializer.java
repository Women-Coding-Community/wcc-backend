package com.wcc.platform.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.Year;

/** Serializer for Year that outputs as integer. */
public class YearSerializer extends JsonSerializer<Year> {

  @Override
  public void serialize(final Year value, final JsonGenerator gen, final SerializerProvider serializers)
      throws IOException {
    if (value != null) {
      gen.writeNumber(value.getValue());
    } else {
      gen.writeNull();
    }
  }
}
