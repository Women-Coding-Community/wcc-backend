package com.wcc.platform.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import com.wcc.platform.deserializers.CmsIconDeserializer;
import com.wcc.platform.deserializers.ImageTypeDeserializer;
import com.wcc.platform.deserializers.MemberTypeDeserializer;
import com.wcc.platform.deserializers.ProgramTypeDeserializer;
import com.wcc.platform.deserializers.SocialNetworkTypeDeserializer;
import com.wcc.platform.deserializers.ZonedDateTimeDeserializer;
import com.wcc.platform.domain.cms.attributes.CmsIcon;
import com.wcc.platform.domain.cms.attributes.ImageType;
import com.wcc.platform.domain.platform.MemberType;
import com.wcc.platform.domain.platform.ProgramType;
import com.wcc.platform.domain.platform.SocialNetworkType;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** ObjectMapperConfig. */
@Configuration
public class ObjectMapperConfig {

  /** Create ObjectMapper bean and include custom serializer. */
  @Bean
  public ObjectMapper objectMapper() {
    final ObjectMapper objectMapper = new ObjectMapper();

    objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    registerCustomDeserializers(objectMapper);
    return objectMapper;
  }

  private void registerCustomDeserializers(final ObjectMapper objectMapper) {
    final DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern("EEE, MMM dd, yyyy, h:mm a z", Locale.ENGLISH);

    final JavaTimeModule javaTimeModule = new JavaTimeModule();
    javaTimeModule.addDeserializer(ZonedDateTime.class, new ZonedDateTimeDeserializer(formatter));
    javaTimeModule.addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer(formatter));

    objectMapper
        .registerModule(javaTimeModule)
        .registerModule(
            new SimpleModule()
                .addDeserializer(ProgramType.class, new ProgramTypeDeserializer())
                .addDeserializer(MemberType.class, new MemberTypeDeserializer())
                .addDeserializer(ImageType.class, new ImageTypeDeserializer())
                .addDeserializer(CmsIcon.class, new CmsIconDeserializer())
                .addDeserializer(SocialNetworkType.class, new SocialNetworkTypeDeserializer()));
  }
}
