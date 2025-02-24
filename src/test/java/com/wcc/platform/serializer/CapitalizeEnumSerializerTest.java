package com.wcc.platform.serializer;

import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CapitalizeEnumSerializerTest {

  private CapitalizeEnumSerializer serializer;
  private JsonGenerator jsonGenerator;
  private SerializerProvider serializerProvider;

  @BeforeEach
  void setUp() {
    serializer = new CapitalizeEnumSerializer();
    jsonGenerator = Mockito.mock(JsonGenerator.class);
    serializerProvider = Mockito.mock(SerializerProvider.class);
  }

  @Test
  void testCapitalizeEnumValue() throws IOException {
    TestEnum testEnum = TestEnum.PARTNER;

    serializer.serialize(testEnum, jsonGenerator, serializerProvider);

    String expectedResult = "Partner";
    verify(jsonGenerator).writeString(expectedResult);
  }

  private enum TestEnum {
    PARTNER
  }
}
