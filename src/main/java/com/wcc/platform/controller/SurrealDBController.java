package com.wcc.platform.controller;

import com.wcc.platform.surrealDB.SurrealDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SurrealDBController {

  private final SurrealDBService surrealDBService;

  @Autowired
  public SurrealDBController(SurrealDBService surrealDBService) {
    this.surrealDBService = surrealDBService;
  }

  @GetMapping("/query-websocket")
  public String queryViaWebSocket() {
    try {
      surrealDBService.connectAndQuery();
      return "Query sent via WebSocket!";
    } catch (Exception e) {
      return "Error: " + e.getMessage();
    }
  }
}
