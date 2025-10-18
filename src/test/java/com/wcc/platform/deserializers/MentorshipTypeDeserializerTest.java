package com.wcc.platform.deserializers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class MentorshipTypeDeserializerTest {
  @Mock private JsonParser jsonParser;

  @Mock private DeserializationContext context;

  @InjectMocks private MentorshipTypeDeserializer deserializer;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @ParameterizedTest
  @EnumSource(MentorshipType.class)
  void testDeserialize(final MentorshipType type) throws IOException {
    when(jsonParser.getText()).thenReturn(type.toString());

    var response = deserializer.deserialize(jsonParser, context);

    assertEquals(type, response);
  }

  @Test
  void testDeserializeInvalid() throws IOException {
    when(jsonParser.getText()).thenReturn("UNDEFINED");

    var response = deserializer.deserialize(jsonParser, context);

    assertEquals(MentorshipType.AD_HOC, response);
  }
}
