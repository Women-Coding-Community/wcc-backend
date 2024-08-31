package com.wcc.platform.configuration;

import com.surrealdb.driver.SyncSurrealDriver;
import com.wcc.platform.repository.ResourceContentRepository;
import com.wcc.platform.repository.surrealdb.SurrealDbResourceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Repository Configurations to initialize respective implementation. */
@Configuration
public class RepositoryConfig {

  /** Default repository of resources. */
  @Bean
  public ResourceContentRepository getResourceRepository(final SyncSurrealDriver driver) {
    return new SurrealDbResourceRepository(driver);
  }
}
