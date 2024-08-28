package com.wcc.platform.configuration;

import com.surrealdb.connection.SurrealConnection;
import com.surrealdb.connection.SurrealWebSocketConnection;
import com.surrealdb.driver.SyncSurrealDriver;
import com.wcc.platform.domain.cms.pages.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

/** Setup Surreal connection configuration. */
@Slf4j
public class SurrealDBConfig {

  /** Get connection and test. */
  @Bean
  public SurrealConnection surrealDBClient() {
    SurrealConnection conn = new SurrealWebSocketConnection("localhost", 8000, false);

    conn.connect(5);

    SyncSurrealDriver driver = new SyncSurrealDriver(conn);
    driver.signIn("root", "root");
    driver.use("wcc", "platform");
    String tableName = "page";
    driver.delete(tableName);

    driver.create(tableName, Page.builder().title("page1").build());
    driver.create(tableName, Page.builder().title("page2").build());

    var pages = driver.select(tableName, Page.class);
    log.info("All pages {}", pages);

    return conn;
  }
}
