package com.wcc.platform.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.file.FileMemberRepository;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** RepositoryConfig. */
@Configuration
public class RepositoryConfig {

  /** Create FileMemberRepository bean. */
  @Bean
  public MemberRepository createFileRepository(final ObjectMapper objectMapper) {
    try {
      return new FileMemberRepository(objectMapper);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
