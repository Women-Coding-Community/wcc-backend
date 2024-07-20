package com.wcc.platform.deserializers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.wcc.platform.domain.platform.MemberType;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class MemberTypeDeserializerTest {

  @Mock private JsonParser jsonParser;

  @Mock private DeserializationContext context;

  @InjectMocks private MemberTypeDeserializer deserializer;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @ParameterizedTest
  @EnumSource(MemberType.class)
  void testDeserialize(final MemberType type) throws IOException {
    when(jsonParser.getText()).thenReturn(type.name());

    var response = deserializer.deserialize(jsonParser, context);

    assertEquals(type, response);
  }

  void testDeserializeInvalid() throws IOException {
    when(jsonParser.getText()).thenReturn("UNDEFINED");

    var response = deserializer.deserialize(jsonParser, context);

    assertEquals(MemberType.MEMBER, response);
  }
}
