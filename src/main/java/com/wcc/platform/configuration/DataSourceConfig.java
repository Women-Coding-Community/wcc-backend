package com.wcc.platform.configuration;

import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Database configuration to initialize DataSource for PostgreSQL and SurrealDB.
 *
 * <p>PostgreSQL is the primary database, while SurrealDB is used for secondary purposes.
 */
@Configuration
public class DataSourceConfig {

  @Bean
  public DataSource primaryDataSource(final DataSourceProperties properties) {
    return properties.initializeDataSourceBuilder().build();
  }
}
