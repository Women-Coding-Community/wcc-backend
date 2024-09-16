package com.wcc.platform.surrealDB;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class SurrealDBWebSocketClient extends TextWebSocketHandler {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private WebSocketSession session;

  public CompletableFuture<WebSocketSession> connect(String uri) throws Exception {
    CompletableFuture<WebSocketSession> future = new CompletableFuture<>();
    StandardWebSocketClient client = new StandardWebSocketClient();
    WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
    headers.add("surreal-ns", "mynamespace");
    headers.add("surreal-db", "mydatabase");

    future = client.execute(this, headers, URI.create(uri));

    return future;
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) {
    this.session = session;
  }

  public void sendMessage(String message) throws Exception {
    if (session != null && session.isOpen()) {
      session.sendMessage(new org.springframework.web.socket.TextMessage(message));
    }
  }

  @Override
  protected void handleTextMessage(
      WebSocketSession session, org.springframework.web.socket.TextMessage message)
      throws Exception {
    String payload = message.getPayload();
    JsonNode jsonNode = objectMapper.readTree(payload);
    System.out.println("Received response: " + jsonNode.toPrettyString());
  }

  public void disconnect() throws Exception {
    if (session != null && session.isOpen()) {
      session.close();
    }
  }
}
