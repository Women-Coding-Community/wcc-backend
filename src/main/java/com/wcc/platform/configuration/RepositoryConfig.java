package com.wcc.platform.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.file.FileMemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Repository Configurations to initialize respective implementation. */
@Configuration
public class RepositoryConfig {

  /** Create FileMemberRepository bean. */
  @Bean
  public MemberRepository createFileRepository(
      final ObjectMapper objectMapper,
      @Value("${file.storage.directory}") final String directoryPath) {
    return new FileMemberRepository(objectMapper, directoryPath);
  }
}
