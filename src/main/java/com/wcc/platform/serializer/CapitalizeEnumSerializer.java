package com.wcc.platform.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.Locale;
import org.springframework.util.StringUtils;

/** Custom capitalization serializer for enum classes. */
public class CapitalizeEnumSerializer extends JsonSerializer<Enum<?>> {

  @Override
  public void serialize(
      final Enum<?> value, final JsonGenerator gen, final SerializerProvider serializers)
      throws IOException {
    gen.writeString(StringUtils.capitalize(value.name().toLowerCase(Locale.ROOT)));
  }
}
