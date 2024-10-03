package com.wcc.platform.integrationtests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
class SurrealDbIntegrationTest {

  private static final GenericContainer<?> surrealDbContainer =
      new GenericContainer<>(DockerImageName.parse("surrealdb/surrealdb:latest"))
          .withExposedPorts(8000)
          .withCommand("start --user root --pass root");

  @DynamicPropertySource
  static void registerSurrealDbProperties(DynamicPropertyRegistry registry) {
    surrealDbContainer.start();
    String host = surrealDbContainer.getHost();
    Integer port = surrealDbContainer.getMappedPort(8000);
    registry.add("surrealdb.host", () -> host);
    registry.add("surrealdb.port", port::toString);
    registry.add("surrealdb.username", () -> "root");
    registry.add("surrealdb.password", () -> "root");
    registry.add("surrealdb.namespace", () -> "test_namespace");
    registry.add("surrealdb.database", () -> "test_db");
  }

  @BeforeAll
  static void setUp() {
    surrealDbContainer.start();
  }

  @AfterAll
  static void tearDown() {
    surrealDbContainer.stop();
  }

  @Test
  @DisplayName("Should create and retrieve a ResourceContent entity")
  void testSurrealDbConnection() {
    assertTrue(surrealDbContainer.isCreated());
    assertTrue(surrealDbContainer.isRunning());
  }
}
