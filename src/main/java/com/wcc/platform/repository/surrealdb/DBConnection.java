package com.wcc.platform.repository.surrealdb;

import com.surrealdb.connection.SurrealConnection;
import com.surrealdb.connection.SurrealWebSocketConnection;
import com.surrealdb.driver.SyncSurrealDriver;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class DBConnection {

  private SyncSurrealDriver driver = null;

  public DBConnection() {
    SurrealConnection conn = new SurrealWebSocketConnection("localhost", 8000, false);
    conn.connect(5);
    driver = new SyncSurrealDriver(conn);
    driver.use("spring", "todo");
  }
}
