package com.wcc.platform.surrealDB;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SurrealDBService {

  private final SurrealDBWebSocketClient webSocketClient;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  public SurrealDBService(SurrealDBWebSocketClient webSocketClient) {
    this.webSocketClient = webSocketClient;
  }

  public void connectAndQuery() throws Exception {
    String uri = "ws://surrealdb:8000/rpc"; // SurrealDB WebSocket URL
    SurrealDBQuery query =
        new SurrealDBQuery("1", "query", List.of("SELECT * FROM user WHERE name = 'Sonali';"));

    webSocketClient
        .connect(uri)
        .thenAccept(
            connection -> {
              System.out.println(connection);
              try {
                // Construct a query (Example: selecting all users)
                String jsonQuery = objectMapper.writeValueAsString(query);
                webSocketClient.sendMessage(jsonQuery);
              } catch (Exception e) {
                e.printStackTrace();
              }
            })
        .exceptionally(
            ex -> {
              System.err.println("Failed to connect: " + ex.getMessage());
              return null;
            });
  }

  public void disconnect() throws Exception {
    webSocketClient.disconnect();
  }
}
