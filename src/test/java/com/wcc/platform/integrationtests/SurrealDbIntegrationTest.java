package com.wcc.platform.integrationtests;

import static com.wcc.platform.factories.SetupFactories.createLinkTest;
import static com.wcc.platform.factories.SetupFactories.createNetworksTest;
import static org.junit.jupiter.api.Assertions.*;

import com.surrealdb.connection.SurrealConnection;
import com.surrealdb.connection.SurrealWebSocketConnection;
import com.surrealdb.driver.SyncSurrealDriver;
import com.wcc.platform.domain.cms.attributes.LabelLink;
import com.wcc.platform.domain.cms.attributes.Network;
import com.wcc.platform.domain.cms.pages.FooterPage;
import com.wcc.platform.repository.surrealdb.SurrealDbPageRepository;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.*;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class SurrealDbIntegrationTest {
  @Container
  static final GenericContainer<?> surrealDbContainer =
      new GenericContainer<>("surrealdb/surrealdb:latest")
          .withExposedPorts(8000)
          .withCommand("start", "--log", "debug", "--user", "root", "--pass", "password")
          .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("surrealDbContainer")));

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
    connection.connect(120); // timeout second

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
    repository = new SurrealDbPageRepository(driver, FooterPage.class);
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
    FooterPage page1 =
        new FooterPage(
            "id", "footer_title", "footer_subtitle", "footer_description", networks, link);

    FooterPage page2 =
        new FooterPage(
            "id1", "footer1_title", "footer1_subtitle", "footer1_description", networks, link);

    // Act
    repository.save(page1);
    repository.save(page2);
    Collection<FooterPage> pages = repository.findAll();

    // Assert
    assertNotNull(pages);
    assertEquals(2, pages.size());

    assertTrue(pages.stream().anyMatch(page -> page.id().equals(TABLE + ":" + page1.id())));
    assertTrue(pages.stream().anyMatch(page -> page.id().equals(TABLE + ":" + page2.id())));
  }
}
