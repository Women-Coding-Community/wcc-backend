package com.wcc.platform.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.springframework.util.StringUtils;

/** Custom capitalization serializer for enum classes from String value. */
public class CapitalizeEnumFromStringSerializer extends JsonSerializer<Enum<?>> {

  @Override
  public void serialize(
      final Enum<?> value, final JsonGenerator gen, final SerializerProvider serializers)
      throws IOException {
    gen.writeString(StringUtils.capitalize(value.toString()));
  }
}
