package com.wcc.platform.integrationtests;

import static com.wcc.platform.factories.SetupFactories.createLinkTest;
import static com.wcc.platform.factories.SetupFactories.createNetworksTest;
import static org.junit.jupiter.api.Assertions.*;

import com.surrealdb.connection.SurrealConnection;
import com.surrealdb.connection.SurrealWebSocketConnection;
import com.surrealdb.driver.SyncSurrealDriver;
import com.wcc.platform.domain.cms.PageType;
import com.wcc.platform.domain.cms.attributes.LabelLink;
import com.wcc.platform.domain.cms.attributes.Network;
import com.wcc.platform.repository.surrealdb.SurrealDbPageRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class SurrealDbIntegrationTest {
  @Container
  static final GenericContainer<?> surrealDbContainer =
      new GenericContainer<>("surrealdb/surrealdb:latest")
          .withExposedPorts(8000)
          .withCommand("start", "--log", "debug", "--user", "root", "--pass", "password");

  private static final String TABLE = "page";

  @BeforeAll
  static void setUpContainer() {
    surrealDbContainer.start();
  }

  @AfterAll
  static void tearDownContainer() {
    surrealDbContainer.stop();
  }

  @Test
  void testSaveAndFindAll() {
    String host = surrealDbContainer.getHost();
    Integer port = surrealDbContainer.getFirstMappedPort();

    SurrealConnection connection = new SurrealWebSocketConnection(host, port, false);
    connection.connect(120); // timeout second

    SyncSurrealDriver driver = new SyncSurrealDriver(connection);
    driver.signIn("root", "password");
    driver.use("test", "test");
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
    connection.disconnect();
  }
}
