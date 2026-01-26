package com.wcc.platform.configuration;

import com.wcc.platform.configuration.converter.StringToEnumConverter;
import com.wcc.platform.configuration.converter.StringToEnumConverterFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Adjust CORS configuration to allow specific server to access the API's. */
@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Value("${backend.app.url}")
  private String appBaseUrl;

  @Override
  public void addCorsMappings(final CorsRegistry registry) {
    registry
        .addMapping("/**")
        .allowedOrigins(appBaseUrl)
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("*");
  }

  @Override
  public void addFormatters(final FormatterRegistry registry) {
    registry.addConverter(new StringToEnumConverter());
    registry.addConverterFactory(new StringToEnumConverterFactory());
  }
}
