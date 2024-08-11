package com.wcc.platform.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.wcc.platform.domain.cms.attributes.CmsIcon;
import java.io.IOException;

/** Custom Deserialize for CMS ICONS. */
public class CmsIconDeserializer extends JsonDeserializer<CmsIcon> {
  @Override
  public CmsIcon deserialize(final JsonParser parser, final DeserializationContext context)
      throws IOException {
    final String value = parser.getText();

    for (final CmsIcon icon : CmsIcon.values()) {
      if (icon.getClassName().equals(value)) {
        return icon;
      }
    }

    throw new IllegalArgumentException("Unknown value: " + value);
  }
}
