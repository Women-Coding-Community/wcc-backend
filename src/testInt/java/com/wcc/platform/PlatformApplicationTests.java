package com.wcc.platform;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.wcc.platform.repository.postgres.DefaultDatabaseSetup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

class PlatformApplicationTests extends DefaultDatabaseSetup {

  @Autowired private ApplicationContext context;

  @Test
  void contextLoads() {
    assertNotNull(context, "The application context should have loaded");
  }
}
