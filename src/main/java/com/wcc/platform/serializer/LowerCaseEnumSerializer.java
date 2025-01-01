package com.wcc.platform.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.Locale;

/** Custom Serialize for enum classes as lower case. */
public class LowerCaseEnumSerializer extends JsonSerializer<Enum<?>> {

  @Override
  public void serialize(
      final Enum<?> value, final JsonGenerator gen, final SerializerProvider serializers)
      throws IOException {

    if (value == null) {
      gen.writeString("null");
    } else {
      gen.writeString(value.name().toLowerCase(Locale.ENGLISH));
    }
  }
}
