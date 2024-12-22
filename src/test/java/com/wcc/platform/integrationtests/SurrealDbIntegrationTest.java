package com.wcc.platform.integrationtests;

import static com.wcc.platform.factories.SetupFactories.createLinkTest;
import static com.wcc.platform.factories.SetupFactories.createNetworksTest;
import static org.junit.jupiter.api.Assertions.*;

import com.surrealdb.driver.SyncSurrealDriver;
import com.wcc.platform.domain.cms.PageType;
import com.wcc.platform.domain.cms.attributes.LabelLink;
import com.wcc.platform.domain.cms.attributes.Network;
import com.wcc.platform.repository.surrealdb.SurrealDbPageRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.*;
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

  @Test
  void testSaveAndFindAll() {
    SurrealDbPageRepository repository = new SurrealDbPageRepository(driver);
    // Arrange
    List<Network> networks = createNetworksTest();
    LabelLink link = createLinkTest();

    Map<String, Object> map =
        Map.of(
            "id",
            "page:FOOTER",
            "title",
            "footer_title",
            "subtitle",
            "footer_subtitle",
            "description",
            "footer_description",
            "network",
            networks,
            "link",
            link);

    // Act
    repository.create(map);
    Optional<Map<String, Object>> page = repository.findById(PageType.FOOTER.getPageId());

    // Assert
    assertTrue(page.isPresent());
    assertEquals(6, page.get().size());
    assertTrue(page.get().containsValue(PageType.FOOTER.getPageId()));

    driver.delete(TABLE);
  }
}
