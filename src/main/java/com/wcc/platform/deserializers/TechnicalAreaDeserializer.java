package com.wcc.platform.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.wcc.platform.domain.cms.attributes.TechnicalArea;
import java.io.IOException;
import java.util.Arrays;

/** Custom deserializer for {@link TechnicalArea} enum. */
public class TechnicalAreaDeserializer extends JsonDeserializer<TechnicalArea> {

  @Override
  public TechnicalArea deserialize(
      final JsonParser jsonParser, final DeserializationContext context) {
    try {
      final var value = jsonParser.getText();

      return Arrays.stream(TechnicalArea.values())
          .filter(technicalArea -> technicalArea.name().equalsIgnoreCase(value))
          .findFirst()
          .orElse(TechnicalArea.OTHER);

    } catch (IOException ex) {
      return TechnicalArea.OTHER;
    }
  }
}
