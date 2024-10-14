package com.wcc.platform.repository.surrealdb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SurrealDbConfigTest {

  @Test
  @DisplayName("Given NoArgsConstructor, when initialized, then fields have default values")
  void givenNoArgsConstructorWhenInitializedThenFieldsHaveDefaultValues() {
    SurrealDbConfig config = new SurrealDbConfig();

    assertNotNull(config);
    assertNull(config.getHost());
    assertEquals(0, config.getPort());
    assertFalse(config.isTls());
    assertNull(config.getUsername());
    assertNull(config.getPassword());
    assertEquals(0, config.getTimeoutSeconds());
    assertNull(config.getNamespace());
    assertNull(config.getDatabase());
  }

  @Test
  @DisplayName("Given Setters, when fields are set, then values are correctly assigned")
  void givenSettersWhenFieldsAreSetThenValuesAreCorrectlyAssigned() {
    SurrealDbConfig config = new SurrealDbConfig();

    config.setHost("localhost");
    config.setPort(8000);
    config.setTls(true);
    config.setUsername("admin");
    config.setPassword("password");
    config.setTimeoutSeconds(10);
    config.setNamespace("test_ns");
    config.setDatabase("test_db");

    assertEquals("localhost", config.getHost());
    assertEquals(8000, config.getPort());
    assertTrue(config.isTls());
    assertEquals("admin", config.getUsername());
    assertEquals("password", config.getPassword());
    assertEquals(10, config.getTimeoutSeconds());
    assertEquals("test_ns", config.getNamespace());
    assertEquals("test_db", config.getDatabase());
  }

  @Test
  @DisplayName(
      "Given Partial Setters, when some fields are set, then unset fields have default values")
  void givenPartialSettersWhenSomeFieldsAreSetThenUnsetFieldsHaveDefaultValues() {
    SurrealDbConfig config = new SurrealDbConfig();

    config.setHost("localhost");
    config.setPort(1234);

    assertEquals("localhost", config.getHost());
    assertEquals(1234, config.getPort());
    assertFalse(config.isTls());
    assertNull(config.getUsername());
    assertNull(config.getPassword());
    assertEquals(0, config.getTimeoutSeconds());
    assertNull(config.getNamespace());
    assertNull(config.getDatabase());
  }

  @Test
  @DisplayName("Given ToString, when called, then returns correct string representation")
  void givenToStringWhenCalledThenReturnsCorrectStringRepresentation() {
    SurrealDbConfig config = new SurrealDbConfig();
    config.setHost("localhost");
    config.setPort(8000);
    config.setTls(true);
    config.setUsername("admin");
    config.setPassword("password");
    config.setTimeoutSeconds(10);
    config.setNamespace("test_ns");
    config.setDatabase("test_db");

    String expected =
        "SurrealDbConfig(host=localhost, port=8000, tls=true, username=admin, "
            + "password=password, timeoutSeconds=10, namespace=test_ns, database=test_db)";

    assertEquals(expected, config.toString());
  }

  @Test
  @DisplayName(
      "Given Equals and HashCode, when fields are identical, "
          + "then objects are equal and hash codes match")
  void givenEqualsAndHashCodeWhenFieldsAreIdenticalThenObjectsAreEqualAndHashCodesMatch() {
    SurrealDbConfig config1 = new SurrealDbConfig();
    config1.setHost("localhost");
    config1.setPort(8000);
    config1.setTls(true);
    config1.setUsername("admin");
    config1.setPassword("password");
    config1.setTimeoutSeconds(10);
    config1.setNamespace("test_ns");
    config1.setDatabase("test_db");

    SurrealDbConfig config2 = new SurrealDbConfig();
    config2.setHost("localhost");
    config2.setPort(8000);
    config2.setTls(true);
    config2.setUsername("admin");
    config2.setPassword("password");
    config2.setTimeoutSeconds(10);
    config2.setNamespace("test_ns");
    config2.setDatabase("test_db");

    assertEquals(config1, config2);
    assertEquals(config1.hashCode(), config2.hashCode());
  }
}
