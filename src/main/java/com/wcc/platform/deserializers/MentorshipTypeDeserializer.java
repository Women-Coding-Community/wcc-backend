package com.wcc.platform.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.wcc.platform.domain.cms.attributes.MentorshipFocusArea;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import java.io.IOException;
import java.util.Arrays;

/** Custom deserializer for {@link MentorshipFocusArea} enum. */
public class MentorshipTypeDeserializer extends JsonDeserializer<MentorshipType> {

  @Override
  public MentorshipType deserialize(
      final JsonParser jsonParser, final DeserializationContext context) {
    try {
      final var value = jsonParser.getText();

      return Arrays.stream(MentorshipType.values())
          .filter(
              area ->
                  area.getDescription().equalsIgnoreCase(value)
                      || area.name().equalsIgnoreCase(value))
          .findFirst()
          .orElse(MentorshipType.AD_HOC);

    } catch (IOException ex) {
      return MentorshipType.AD_HOC;
    }
  }
}
