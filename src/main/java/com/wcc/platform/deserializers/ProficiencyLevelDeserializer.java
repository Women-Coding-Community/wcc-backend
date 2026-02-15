package com.wcc.platform.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.wcc.platform.domain.cms.attributes.ProficiencyLevel;
import java.io.IOException;
import java.util.Arrays;

/** Custom deserializer for {@link ProficiencyLevel} enum. */
public class ProficiencyLevelDeserializer extends JsonDeserializer<ProficiencyLevel> {

  @Override
  public ProficiencyLevel deserialize(
      final JsonParser jsonParser, final DeserializationContext context) {
    try {
      final var value = jsonParser.getText();

      return Arrays.stream(ProficiencyLevel.values())
          .filter(
              level ->
                  level.getDescription().equalsIgnoreCase(value)
                      || level.name().equalsIgnoreCase(value)
                      || level.getLevelId().toString().equals(value))
          .findFirst()
          .orElse(ProficiencyLevel.UNDEFINED);

    } catch (IOException ex) {
      return ProficiencyLevel.UNDEFINED;
    }
  }
}
