package com.wcc.platform;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.wcc.platform.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

/**
 * A simple test that just verifies that the application context loads correctly.
 * This test doesn't use any of the problematic beans.
 */
@SpringBootTest(classes = {TestConfig.class})
@ActiveProfiles("test")
class SimpleContextLoadTest {

  @Autowired private ApplicationContext context;

  @Test
  void contextLoads() {
    assertNotNull(context, "The application context should have loaded");
  }
}
