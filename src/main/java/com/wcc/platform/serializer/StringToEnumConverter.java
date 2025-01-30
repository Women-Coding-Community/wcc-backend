package com.wcc.platform.serializer;

import com.wcc.platform.domain.platform.ProgramType;
import org.springframework.core.convert.converter.Converter;

public class StringToEnumConverter implements Converter<String, ProgramType> {
  @Override
  public ProgramType convert(final String description) {
    return ProgramType.findByValue(description);
  }
}
