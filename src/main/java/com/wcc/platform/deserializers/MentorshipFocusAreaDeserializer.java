package com.wcc.platform.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.wcc.platform.domain.cms.attributes.MentorshipFocusArea;
import java.io.IOException;
import java.util.Arrays;

/** Custom deserializer for {@link MentorshipFocusArea} enum. */
public class MentorshipFocusAreaDeserializer extends JsonDeserializer<MentorshipFocusArea> {

  @Override
  public MentorshipFocusArea deserialize(
      final JsonParser jsonParser, final DeserializationContext context) {
    try {
      final var value = jsonParser.getText();

      return Arrays.stream(MentorshipFocusArea.values())
          .filter(
              area ->
                  area.getDescription().equalsIgnoreCase(value)
                      || area.name().equalsIgnoreCase(value))
          .findFirst()
          .orElse(MentorshipFocusArea.SWITCH_CAREER_TO_IT);

    } catch (IOException ex) {
      return MentorshipFocusArea.SWITCH_CAREER_TO_IT;
    }
  }
}
