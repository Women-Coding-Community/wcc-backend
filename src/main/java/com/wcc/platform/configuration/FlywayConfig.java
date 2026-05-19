package com.wcc.platform.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Flyway configuration for migration repair and execution. */
@Configuration
public class FlywayConfig {

  /**
   * Runs {@code flyway.repair()} before each migration when the property {@code
   * flyway.repair-on-migrate} is {@code true}.
   *
   * <p>Use this temporarily to fix checksum mismatches caused by migration files being modified
   * after they were already applied (e.g. via {@code FLYWAY_REPAIR_ON_MIGRATE=true} on Fly.io).
   * Remove the environment variable after the repair has run successfully in production.
   *
   * @return a {@link FlywayMigrationStrategy} that repairs then migrates
   */
  @Bean
  @ConditionalOnProperty(name = "flyway.repair-on-migrate", havingValue = "true")
  public FlywayMigrationStrategy repairThenMigrate() {
    return flyway -> {
      flyway.repair();
      flyway.migrate();
    };
  }
}
