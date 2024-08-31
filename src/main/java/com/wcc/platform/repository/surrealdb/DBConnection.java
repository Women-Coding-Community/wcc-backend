package com.wcc.platform.repository.surrealdb;

import com.surrealdb.connection.SurrealWebSocketConnection;
import com.surrealdb.driver.SyncSurrealDriver;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Component
public class DBConnection {

  private final SyncSurrealDriver driver;

  @Autowired
  public DBConnection(final SurrealDBConfig config) {
    final var conn =
        new SurrealWebSocketConnection(config.getHost(), config.getPort(), config.isTls());
    conn.connect(config.getConnections());
    driver = new SyncSurrealDriver(conn);
    driver.use(config.getNamespace(), config.getDatabase());
  }
}
