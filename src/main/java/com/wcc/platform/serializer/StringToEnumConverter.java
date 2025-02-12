package com.wcc.platform.serializer;

import com.wcc.platform.domain.platform.ProgramType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;

/** Convert String type (description used as a RequestParam for programs) to Enum ProgramType. */
public class StringToEnumConverter implements Converter<String, ProgramType> {
  @Override
  @Nullable
  public ProgramType convert(@Nullable final String description) {
    return ProgramType.findByValue(description);
  }
}
