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
  private static SurrealConnection connection;
  private static SyncSurrealDriver driver;
  private SurrealDbPageRepository repository;

  @BeforeAll
  static void setUpContainer() {
    surrealDbContainer.start();

    // Initialize SyncSurrealDriver with container connection
    String host = surrealDbContainer.getHost();
    Integer port = surrealDbContainer.getFirstMappedPort();

    connection = new SurrealWebSocketConnection(host, port, false);
    connection.connect(1200); // timeout second

    driver = new SyncSurrealDriver(connection);
    driver.signIn("root", "password");
    driver.use("test", "test");
  }

  @AfterAll
  static void tearDownContainer() {
    connection.disconnect();
    surrealDbContainer.stop();
  }

  @BeforeEach
  void setUp() {
    repository = new SurrealDbPageRepository(driver);
  }

  @AfterEach
  void tearDown() {
    driver.delete(TABLE);
  }

  @Test
  void testSaveAndFindAll() {
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
  }
}
