package com.wcc.platform.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import com.wcc.platform.deserializers.*;
import com.wcc.platform.domain.cms.attributes.CmsIcon;
import com.wcc.platform.domain.cms.attributes.ImageType;
import com.wcc.platform.domain.cms.attributes.Languages;
import com.wcc.platform.domain.cms.attributes.MentorshipFocusArea;
import com.wcc.platform.domain.cms.attributes.TechnicalArea;
import com.wcc.platform.domain.cms.attributes.style.ColorShadeType;
import com.wcc.platform.domain.cms.attributes.style.ColorType;
import com.wcc.platform.domain.platform.SocialNetworkType;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import com.wcc.platform.domain.platform.type.MemberType;
import com.wcc.platform.domain.platform.type.ProgramType;
import com.wcc.platform.serializer.CapitalizeEnumFromStringSerializer;
import com.wcc.platform.serializer.CapitalizeEnumSerializer;
import com.wcc.platform.serializer.LowerCaseEnumSerializer;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** ObjectMapperConfig. */
@Configuration
public class ObjectMapperConfig {

  public static final String DATE_TIME_FORMAT = "EEE, MMM dd, yyyy, h:mm a z";

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
        DateTimeFormatter.ofPattern(DATE_TIME_FORMAT, Locale.ENGLISH);
    objectMapper
        .registerModule(
            new JavaTimeModule()
                .addDeserializer(ZonedDateTime.class, new ZonedDateTimeDeserializer(formatter))
                .addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer(formatter)))
        .registerModule(
            new SimpleModule()
                .addSerializer(ImageType.class, new LowerCaseEnumSerializer())
                .addSerializer(ColorType.class, new LowerCaseEnumSerializer())
                .addSerializer(ColorShadeType.class, new LowerCaseEnumSerializer())
                .addSerializer(Languages.class, new CapitalizeEnumFromStringSerializer())
                .addSerializer(TechnicalArea.class, new CapitalizeEnumFromStringSerializer())
                .addSerializer(MentorshipFocusArea.class, new CapitalizeEnumFromStringSerializer())
                .addSerializer(MentorshipType.class, new CapitalizeEnumFromStringSerializer())
                .addSerializer(SocialNetworkType.class, new LowerCaseEnumSerializer())
                .addSerializer(MemberType.class, new CapitalizeEnumSerializer())
                .addDeserializer(ProgramType.class, new ProgramTypeDeserializer())
                .addDeserializer(MemberType.class, new MemberTypeDeserializer())
                .addDeserializer(ColorType.class, new ColorTypeDeserializer())
                .addDeserializer(ColorShadeType.class, new ColorShadeTypeDeserializer())
                .addDeserializer(ImageType.class, new ImageTypeDeserializer())
                .addDeserializer(Languages.class, new LanguageDeserializer())
                .addDeserializer(TechnicalArea.class, new TechnicalAreaDeserializer())
                .addDeserializer(MentorshipFocusArea.class, new MentorshipFocusAreaDeserializer())
                .addDeserializer(MentorshipType.class, new MentorshipTypeDeserializer())
                .addDeserializer(CmsIcon.class, new CmsIconDeserializer())
                .addDeserializer(SocialNetworkType.class, new SocialNetworkTypeDeserializer()));
  }
}
