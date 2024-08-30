package com.wcc.platform.configuration;

import com.wcc.platform.repository.ResourceContentRepository;
import com.wcc.platform.repository.surrealdb.DBConnection;
import com.wcc.platform.repository.surrealdb.SurrealDbResourceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {

  /** Default repository of resources * */
  @Bean
  public ResourceContentRepository getResourceRepository(DBConnection connection) {
    return new SurrealDbResourceRepository(connection);
  }
}
