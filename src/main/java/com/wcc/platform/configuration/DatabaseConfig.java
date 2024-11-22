package com.wcc.platform.configuration;

import com.surrealdb.connection.SurrealWebSocketConnection;
import com.surrealdb.driver.SyncSurrealDriver;
import com.wcc.platform.repository.surrealdb.SurrealDbConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Database configuration to initialize Driver and connection. */
@Configuration
public class DatabaseConfig {

  /** Create Sync driver connection for SurrealDB. */
  @Bean
  public SyncSurrealDriver getDriver(final SurrealDbConfig config) {
    final var conn =
        new SurrealWebSocketConnection(config.getHost(), config.getPort(), config.isTls());

    conn.connect(config.getTimeoutSeconds());

    final var driver = new SyncSurrealDriver(conn);

    driver.signIn(config.getUsername(), config.getPassword());

    driver.use(config.getNamespace(), config.getDatabase());

    return driver;
  }

  /** SurrealDB Config. */
  @Bean
  @ConfigurationProperties(prefix = "surrealdb")
  public SurrealDbConfig getDbConfig() {
    return new SurrealDbConfig();
  }
}
