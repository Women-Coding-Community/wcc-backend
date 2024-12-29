package com.wcc.platform.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.wcc.platform.domain.cms.attributes.style.ColorShadeType;
import java.io.IOException;
import java.util.Arrays;

/** Custom deserializer for {@code ColorShadeType} enum. */
public class ColorShadeTypeDeserializer extends JsonDeserializer<ColorShadeType> {

  @Override
  public ColorShadeType deserialize(
      final JsonParser jsonParser, final DeserializationContext context) throws IOException {

    final var value = jsonParser.getText();

    return Arrays.stream(ColorShadeType.values())
        .filter(type -> type.name().equalsIgnoreCase(value))
        .findFirst()
        .orElse(ColorShadeType.MAIN);
  }
}
