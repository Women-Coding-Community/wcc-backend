package com.wcc.platform.serializer;

import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class LowerCaseEnumSerializerTest {

  private LowerCaseEnumSerializer serializer;
  private JsonGenerator jsonGenerator;
  private SerializerProvider serializerProvider;

  @BeforeEach
  void setUp() {
    serializer = new LowerCaseEnumSerializer();
    jsonGenerator = Mockito.mock(JsonGenerator.class);
    serializerProvider = Mockito.mock(SerializerProvider.class);
  }

  @Test
  void serializeEnumValueInLowerCase() throws IOException {
    TestEnum testEnum = TestEnum.VALUE_ONE;

    serializer.serialize(testEnum, jsonGenerator, serializerProvider);

    verify(jsonGenerator).writeString("value_one");
  }

  @Test
  void serializeNullEnumValue() throws IOException {
    serializer.serialize(null, jsonGenerator, serializerProvider);

    verify(jsonGenerator).writeString("null");
  }

  private enum TestEnum {
    VALUE_ONE,
    VALUE_TWO
  }
}
