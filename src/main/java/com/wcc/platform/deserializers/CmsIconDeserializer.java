package com.wcc.platform.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.wcc.platform.domain.cms.attributes.CmsIcon;
import java.io.IOException;

public class CmsIconDeserializer extends JsonDeserializer<CmsIcon> {

  @Override
  public CmsIcon deserialize(final JsonParser p, final DeserializationContext context)
      throws IOException {
    String value = p.getText();

    for (CmsIcon icon : CmsIcon.values()) {
      if (icon.getClassName().equals(value)) {
        return icon;
      }
    }

    throw new IllegalArgumentException("Unknown value: " + value);
  }
}
