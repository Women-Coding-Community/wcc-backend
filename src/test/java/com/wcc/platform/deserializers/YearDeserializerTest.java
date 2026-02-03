package com.wcc.platform.deserializers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import java.io.IOException;
import java.time.Year;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class YearDeserializerTest {

  @Mock private JsonParser jsonParser;
  @Mock private DeserializationContext context;

  private YearDeserializer deserializer;

  @BeforeEach
  void setUp() {
    deserializer = new YearDeserializer();
  }

  @Test
  @DisplayName("Given numeric token, when deserializing, then should return Year from integer")
  void testDeserializeFromNumericValue() throws IOException {
    when(jsonParser.currentToken()).thenReturn(JsonToken.VALUE_NUMBER_INT);
    when(jsonParser.getIntValue()).thenReturn(2026);

    Year result = deserializer.deserialize(jsonParser, context);

    assertNotNull(result);
    assertEquals(Year.of(2026), result);
  }

  @Test
  @DisplayName("Given string token, when deserializing, then should return Year from string")
  void testDeserializeFromStringValue() throws IOException {
    when(jsonParser.currentToken()).thenReturn(JsonToken.VALUE_STRING);
    when(jsonParser.getText()).thenReturn("2026");

    Year result = deserializer.deserialize(jsonParser, context);

    assertNotNull(result);
    assertEquals(Year.of(2026), result);
  }

  @Test
  @DisplayName("Given null token, when deserializing, then should return null")
  void testDeserializeNullValue() throws IOException {
    when(jsonParser.currentToken()).thenReturn(JsonToken.VALUE_NULL);

    Year result = deserializer.deserialize(jsonParser, context);

    assertNull(result);
  }

  @Test
  @DisplayName("Given empty string, when deserializing, then should return null")
  void testDeserializeEmptyString() throws IOException {
    when(jsonParser.currentToken()).thenReturn(JsonToken.VALUE_STRING);
    when(jsonParser.getText()).thenReturn("   ");

    Year result = deserializer.deserialize(jsonParser, context);

    assertNull(result);
  }

  @Test
  @DisplayName("Given invalid token type, when deserializing, then should throw exception")
  void testDeserializeInvalidToken() throws IOException {
    when(jsonParser.currentToken()).thenReturn(JsonToken.START_OBJECT);

    assertThrows(
        IllegalArgumentException.class,
        () -> deserializer.deserialize(jsonParser, context));
  }
}
