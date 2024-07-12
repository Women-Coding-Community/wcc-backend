package com.wcc.platform.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.wcc.platform.domain.platform.SocialNetworkType;
import java.io.IOException;
import java.util.Arrays;

/** Custom deserializer for {@link SocialNetworkType} enum. */
public class SocialNetworkTypeDeserializer extends JsonDeserializer<SocialNetworkType> {

  @Override
  public SocialNetworkType deserialize(
      final JsonParser jsonParser, final DeserializationContext context) {
    try {
      final var value = jsonParser.getText();

      return Arrays.stream(SocialNetworkType.values())
          .filter(memberType -> memberType.name().equalsIgnoreCase(value))
          .findFirst()
          .orElse(SocialNetworkType.UNKNOWN);

    } catch (IOException ex) {
      return SocialNetworkType.UNKNOWN;
    }
  }
}
