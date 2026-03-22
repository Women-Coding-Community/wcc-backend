package com.wcc.platform.deserializers;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.wcc.platform.domain.cms.attributes.ProficiencyLevel;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ProficiencyLevelDeserializerTest {

  private ProficiencyLevelDeserializer deserializer;
  private JsonParser jsonParser;
  private DeserializationContext context;

  @BeforeEach
  void setUp() {
    deserializer = new ProficiencyLevelDeserializer();
    jsonParser = Mockito.mock(JsonParser.class);
    context = Mockito.mock(DeserializationContext.class);
  }

  @Test
  void testDeserializeValidByName() throws IOException {
    Mockito.when(jsonParser.getText()).thenReturn("BEGINNER");
    ProficiencyLevel result = deserializer.deserialize(jsonParser, context);
    assertEquals(ProficiencyLevel.BEGINNER, result);
  }

  @Test
  void testDeserializeValidByDescription() throws IOException {
    Mockito.when(jsonParser.getText()).thenReturn("Beginner");
    ProficiencyLevel result = deserializer.deserialize(jsonParser, context);
    assertEquals(ProficiencyLevel.BEGINNER, result);
  }

  @Test
  void testDeserializeValidById() throws IOException {
    Mockito.when(jsonParser.getText()).thenReturn("1");
    ProficiencyLevel result = deserializer.deserialize(jsonParser, context);
    assertEquals(ProficiencyLevel.BEGINNER, result);
  }

  @Test
  void testDeserializeCaseInsensitive() throws IOException {
    Mockito.when(jsonParser.getText()).thenReturn("beginner");
    ProficiencyLevel result = deserializer.deserialize(jsonParser, context);
    assertEquals(ProficiencyLevel.BEGINNER, result);
  }

  @Test
  void testDeserializeInvalidValueReturnsUndefined() throws IOException {
    Mockito.when(jsonParser.getText()).thenReturn("INVALID");
    assertThrows(
        IllegalArgumentException.class, () -> deserializer.deserialize(jsonParser, context));
  }

  @Test
  void testDeserializeNullValueReturnsUndefined() throws IOException {
    Mockito.when(jsonParser.getText()).thenReturn(null);
    assertThrows(
        IllegalArgumentException.class, () -> deserializer.deserialize(jsonParser, context));
  }

  @Test
  void testDeserializeEmptyStringReturnsUndefined() throws IOException {
    Mockito.when(jsonParser.getText()).thenReturn("");
    assertThrows(
        IllegalArgumentException.class, () -> deserializer.deserialize(jsonParser, context));
  }

  @Test
  void testDeserializeIOExceptionReturnsUndefined() throws IOException {
    Mockito.when(jsonParser.getText()).thenThrow(new IOException("Test exception"));
    assertThrows(
        IllegalArgumentException.class, () -> deserializer.deserialize(jsonParser, context));
  }
}
