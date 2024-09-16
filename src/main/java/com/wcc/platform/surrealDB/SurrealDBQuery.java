package com.wcc.platform.surrealDB;

import java.util.List;

public class SurrealDBQuery {
  private String id;
  private String method;
  private List<String> params;

  // Constructors, Getters, and Setters
  public SurrealDBQuery(String id, String method, List<String> params) {
    this.id = id;
    this.method = method;
    this.params = params;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public List<String> getParams() {
    return params;
  }

  public void setParams(List<String> params) {
    this.params = params;
  }
}
