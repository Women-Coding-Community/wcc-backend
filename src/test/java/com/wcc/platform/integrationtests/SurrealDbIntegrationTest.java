package com.wcc.platform.integrationtests;

import static com.wcc.platform.domain.cms.PageType.FOOTER;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.surrealdb.driver.SyncSurrealDriver;
import com.wcc.platform.repository.surrealdb.SurrealDbPageRepository;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
class SurrealDbIntegrationTest {

  protected static final GenericContainer<?> SURREAL_DB_CONTAINER =
      new GenericContainer<>(DockerImageName.parse("surrealdb/surrealdb:latest"))
          .withExposedPorts(8000)
          .withCommand("start --user root --pass root");
  private static final String TABLE = "page";
  @Autowired private SyncSurrealDriver driver;

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

  @BeforeEach
  void deletePage() {
    final SurrealDbPageRepository repository = new SurrealDbPageRepository(driver);
    repository.deleteById(FOOTER.getId());
  }

  @Test
  void testFindById() {
    final SurrealDbPageRepository repository = new SurrealDbPageRepository(driver);
    Optional<Map<String, Object>> page = repository.findById(FOOTER.getId());

    assertFalse(page.isPresent());
  }
}
