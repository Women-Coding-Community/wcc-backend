package com.wcc.platform.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.wcc.platform.domain.cms.attributes.Languages;
import java.io.IOException;
import java.util.Arrays;

/** Custom deserializer for {@link Languages} enum. */
public class LanguageDeserializer extends JsonDeserializer<Languages> {

  @Override
  public Languages deserialize(final JsonParser jsonParser, final DeserializationContext context) {
    try {
      final var value = jsonParser.getText();

      return Arrays.stream(Languages.values())
          .filter(languages -> languages.name().equalsIgnoreCase(value))
          .findFirst()
          .orElse(Languages.OTHER);

    } catch (IOException ex) {
      return Languages.OTHER;
    }
  }
}
