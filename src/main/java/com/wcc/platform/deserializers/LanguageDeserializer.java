package com.wcc.platform.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.wcc.platform.domain.cms.attributes.CodeLanguage;
import java.io.IOException;
import java.util.Arrays;

/** Custom deserializer for {@link CodeLanguage} enum. */
public class LanguageDeserializer extends JsonDeserializer<CodeLanguage> {

  @Override
  public CodeLanguage deserialize(final JsonParser jsonParser, final DeserializationContext context) {
    try {
      final var value = jsonParser.getText();

      return Arrays.stream(CodeLanguage.values())
          .filter(
              language ->
                  language.getName().equalsIgnoreCase(value)
                      || language.name().equalsIgnoreCase(value))
          .findFirst()
          .orElseThrow(
              () ->
                  new IllegalArgumentException("Invalid technical language with value: " + value));

    } catch (IOException ex) {
      throw new IllegalArgumentException("Invalid technical language {}" + ex.getMessage(), ex);
    }
  }
}
