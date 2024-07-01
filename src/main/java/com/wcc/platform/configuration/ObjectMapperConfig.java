package com.wcc.platform.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wcc.platform.deserializers.ImageTypeDeserializer;
import com.wcc.platform.deserializers.MemberTypeDeserializer;
import com.wcc.platform.deserializers.SocialNetworkTypeDeserializer;
import com.wcc.platform.domain.cms.attributes.ImageType;
import com.wcc.platform.domain.platform.MemberType;
import com.wcc.platform.domain.platform.SocialNetworkType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        registerCustomDeserializers(objectMapper);
        return objectMapper;
    }

    private void registerCustomDeserializers(ObjectMapper objectMapper) {
        objectMapper
                .registerModule(new JavaTimeModule())
                .registerModule(new SimpleModule()
                        .addDeserializer(MemberType.class, new MemberTypeDeserializer())
                        .addDeserializer(ImageType.class, new ImageTypeDeserializer())
                        .addDeserializer(SocialNetworkType.class, new SocialNetworkTypeDeserializer()));
    }
}