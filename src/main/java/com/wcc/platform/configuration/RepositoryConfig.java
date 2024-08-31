package com.wcc.platform.configuration;

import com.wcc.platform.repository.ResourceContentRepository;
import com.wcc.platform.repository.surrealdb.DBConnection;
import com.wcc.platform.repository.surrealdb.SurrealDBConfig;
import com.wcc.platform.repository.surrealdb.SurrealDbResourceRepository;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {

  /** Default repository of resources. */
  @Bean
  public ResourceContentRepository getResourceRepository(final DBConnection connection) {
    return new SurrealDbResourceRepository(connection);
  }

  /** SurrealDB Config. */
  @Bean
  @ConfigurationProperties(prefix = "surrealdb")
  public SurrealDBConfig getDBConfig() {
    return new SurrealDBConfig();
  }
}
