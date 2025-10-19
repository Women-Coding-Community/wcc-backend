package com.wcc.platform.configuration.converter;

import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;

/** Custom converter factory to convert String to Enum. */
@SuppressWarnings("unchecked")
@Component
public class StringToEnumConverterFactory implements ConverterFactory<String, Enum> {

  @Override
  public <T extends Enum> Converter<String, T> getConverter(final Class<T> targetType) {
    return new StringToEnumConverter<>(targetType);
  }

  private static class StringToEnumConverter<T extends Enum<T>> implements Converter<String, T> {

    private final Class<T> enumType;

    public StringToEnumConverter(final Class<T> enumType) {
      this.enumType = enumType;
    }

    @Override
    public T convert(final String source) {
      if (StringUtils.isBlank(source.trim())) {
        return null;
      }

      final String trimmedSource = source.trim();

      try {
        return Enum.valueOf(enumType, trimmedSource.toUpperCase(Locale.ENGLISH));
      } catch (IllegalArgumentException e) {
        for (final T enumConstant : enumType.getEnumConstants()) {
          if (enumConstant.name().equalsIgnoreCase(trimmedSource)) {
            return enumConstant;
          }
        }
        return matchByDisplayName(trimmedSource);
      }
    }

    private T matchByDisplayName(final String source) {
      for (final T enumConstant : enumType.getEnumConstants()) {
        if (enumConstant.toString().equalsIgnoreCase(source)) {
          return enumConstant;
        }
      }
      return null;
    }
  }
}
