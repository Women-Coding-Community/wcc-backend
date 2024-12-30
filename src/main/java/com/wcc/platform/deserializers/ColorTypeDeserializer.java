package com.wcc.platform.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.wcc.platform.domain.cms.attributes.style.ColorType;
import java.io.IOException;
import java.util.Arrays;

/** Custom deserializer for {@code ColorType} enum. */
public class ColorTypeDeserializer extends JsonDeserializer<ColorType> {

  @Override
  public ColorType deserialize(final JsonParser jsonParser, final DeserializationContext context)
      throws IOException {

    final var value = jsonParser.getText();

    return Arrays.stream(ColorType.values())
        .filter(type -> type.name().equalsIgnoreCase(value))
        .findFirst()
        .orElse(ColorType.PRIMARY);
  }
}
