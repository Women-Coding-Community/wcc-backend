package com.wcc.platform.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.file.FileMemberRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {

  @Bean
  public MemberRepository createFileRepository(ObjectMapper objectMapper) {
    return new FileMemberRepository(objectMapper);
  }
}
