package com.wcc.platform.repository.surrealdb;

import lombok.Data;
import lombok.NoArgsConstructor;

/** SurrealDB connection configuration. */
@Data
@NoArgsConstructor
public class SurrealDbConfig {
  private String host;
  private int port;
  private boolean tls;
  private String username;
  private String password;
  private int timeoutSeconds;
  private String namespace;
  private String database;
}
