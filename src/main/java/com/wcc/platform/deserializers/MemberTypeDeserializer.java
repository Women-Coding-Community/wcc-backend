package com.wcc.platform.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.wcc.platform.domain.MemberType;

import java.io.IOException;
import java.util.Arrays;

public class MemberTypeDeserializer extends JsonDeserializer<MemberType> {

    @Override
    public MemberType deserialize(JsonParser jsonParser, DeserializationContext context)
            throws IOException {

        var value = jsonParser.getText();

        return Arrays.stream(MemberType.values())
                .filter(memberType -> memberType.name().equalsIgnoreCase(value))
                .findFirst()
                .orElse(MemberType.MEMBER);
    }
}