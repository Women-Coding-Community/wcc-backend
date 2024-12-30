package com.wcc.platform.deserializers;

import static com.wcc.platform.configuration.ObjectMapperConfig.DATE_TIME_FORMAT;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ZonedDateTimeDeserializerTest {

  private final ZonedDateTimeDeserializer deserializer =
      new ZonedDateTimeDeserializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT, Locale.ENGLISH));

  @Mock private JsonParser jsonParser;
  @Mock private DeserializationContext context;

  @Test
  void testDeserializeValid() throws IOException {
    when(jsonParser.getText()).thenReturn("Thu, May 30, 2024, 9:30 PM CEST");

    var response = deserializer.deserialize(jsonParser, context);

    Assertions.assertNotNull(response);
    Assertions.assertEquals("2024-05-30T21:30+02:00[Europe/Paris]", response.toString());
    Assertions.assertEquals(
        ZonedDateTime.of(2024, 5, 30, 21, 30, 0, 0, ZoneId.of("Europe/Paris")), response);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "Thu, May 30, 2024",
        "2024-05-30T21:30:00",
        "May 30, 2024, 9:30 PM",
        "30-05-2024 21:30",
        "Fri, May 30, 2024, 9:30 PM CEST"
      })
  void testDeserializeInvalidFormat(final String invalidDate) throws IOException {
    when(jsonParser.getText()).thenReturn(invalidDate);

    assertThrows(DateTimeParseException.class, () -> deserializer.deserialize(jsonParser, context));
  }
}
