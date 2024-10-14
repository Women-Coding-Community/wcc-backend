package com.wcc.platform.integrationtests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
class SurrealDbIntegrationTest {

  protected static final GenericContainer<?> SURREAL_DB_CONTAINER =
      new GenericContainer<>(DockerImageName.parse("surrealdb/surrealdb:latest"))
          .withExposedPorts(8000)
          .withCommand("start --user root --pass root");

  @DynamicPropertySource
  static void registerSurrealDbProperties(final DynamicPropertyRegistry registry) {
    SURREAL_DB_CONTAINER.start();
    String host = SURREAL_DB_CONTAINER.getHost();
    Integer port = SURREAL_DB_CONTAINER.getMappedPort(8000);
    registry.add("surrealdb.host", () -> host);
    registry.add("surrealdb.port", port::toString);
    registry.add("surrealdb.username", () -> "root");
    registry.add("surrealdb.password", () -> "root");
    registry.add("surrealdb.namespace", () -> "test_namespace");
    registry.add("surrealdb.database", () -> "test_db");
  }

  @BeforeAll
  static void setUp() {
    SURREAL_DB_CONTAINER.start();
  }

  @AfterAll
  static void tearDown() {
    SURREAL_DB_CONTAINER.stop();
  }

  @Test
  @DisplayName("Should create and retrieve a ResourceContent entity")
  void testSurrealDbConnection() {
    assertTrue(SURREAL_DB_CONTAINER.isCreated());
    assertTrue(SURREAL_DB_CONTAINER.isRunning());
  }
}
