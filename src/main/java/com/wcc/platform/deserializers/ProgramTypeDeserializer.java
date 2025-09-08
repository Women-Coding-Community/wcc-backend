package com.wcc.platform.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.wcc.platform.domain.platform.type.ProgramType;
import java.io.IOException;

/** Custom deserializer for {@code ProgramType} enum. */
public class ProgramTypeDeserializer extends JsonDeserializer<ProgramType> {

  @Override
  public ProgramType deserialize(final JsonParser jsonParser, final DeserializationContext context)
      throws IOException {

    final var value = jsonParser.getText();

    return ProgramType.findByValue(value);
  }
}
