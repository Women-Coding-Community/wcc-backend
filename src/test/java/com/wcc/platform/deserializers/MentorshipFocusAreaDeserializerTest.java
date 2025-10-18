package com.wcc.platform.deserializers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.wcc.platform.domain.cms.attributes.MentorshipFocusArea;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class MentorshipFocusAreaDeserializerTest {
  @Mock private JsonParser jsonParser;

  @Mock private DeserializationContext context;

  @InjectMocks private MentorshipFocusAreaDeserializer deserializer;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @ParameterizedTest
  @EnumSource(MentorshipFocusArea.class)
  void testDeserialize(final MentorshipFocusArea type) throws IOException {
    when(jsonParser.getText()).thenReturn(type.toString());

    var response = deserializer.deserialize(jsonParser, context);

    assertEquals(type, response);
  }

  @Test
  void testDeserializeInvalid() throws IOException {
    when(jsonParser.getText()).thenReturn("UNDEFINED");

    var response = deserializer.deserialize(jsonParser, context);

    assertEquals(MentorshipFocusArea.SWITCH_CAREER_TO_IT, response);
  }
}
