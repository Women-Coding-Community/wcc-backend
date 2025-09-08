package com.wcc.platform.serializer;

import com.wcc.platform.domain.platform.type.ProgramType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

/** Convert String type (description used as a RequestParam for programs) to Enum ProgramType. */
public class StringToEnumConverter implements Converter<String, ProgramType> {
  @Override
  public ProgramType convert(@NonNull final String description) {
    return ProgramType.findByValue(description);
  }
}
