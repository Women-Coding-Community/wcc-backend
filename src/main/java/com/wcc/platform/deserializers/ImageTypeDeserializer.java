package com.wcc.platform.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.wcc.platform.domain.cms.attributes.ImageType;
import java.io.IOException;
import java.util.Arrays;

/** Custom deserializer for ImageType Enum. */
public class ImageTypeDeserializer extends JsonDeserializer<ImageType> {

  @Override
  public ImageType deserialize(final JsonParser jsonParser, final DeserializationContext context)
      throws IOException {

    final var value = jsonParser.getText();

    return Arrays.stream(ImageType.values())
        .filter(type -> type.name().equalsIgnoreCase(value))
        .findFirst()
        .orElse(ImageType.DESKTOP);
  }
}
