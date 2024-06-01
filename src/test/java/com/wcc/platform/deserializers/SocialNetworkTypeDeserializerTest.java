package com.wcc.platform.deserializers;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.wcc.platform.domain.SocialNetworkType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class SocialNetworkTypeDeserializerTest {

    @Mock
    private JsonParser jsonParser;

    @Mock
    private DeserializationContext context;

    @InjectMocks
    private SocialNetworkTypeDeserializer deserializer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @ParameterizedTest
    @EnumSource(SocialNetworkType.class)
    void testDeserialize(SocialNetworkType type) throws IOException {
        when(jsonParser.getText()).thenReturn(type.name());

        var response = deserializer.deserialize(jsonParser, context);

        assertEquals(type, response);
    }

    @Test
    void testDeserializeInvalid() throws IOException {
        when(jsonParser.getText()).thenReturn("UNDEFINED");

        var response = deserializer.deserialize(jsonParser, context);

        assertEquals(SocialNetworkType.UNKNOWN, response);
    }
}