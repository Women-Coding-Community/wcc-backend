package com.wcc.platform.configuration;

import com.wcc.platform.domain.platform.config.PlatformServers;
import com.wcc.platform.serializer.StringToEnumConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Adjust CORS configuration to allow specific server to access the API's. */
@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(final CorsRegistry registry) {
    registry
        .addMapping("/**")
        .allowedOrigins(PlatformServers.LOCAL.getUri(), PlatformServers.DEV.getUri())
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("*");
  }

  @Override
  public void addFormatters(final FormatterRegistry registry) {
    registry.addConverter(new StringToEnumConverter());
  }
}
