package com.wcc.platform;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.wcc.platform.config.TestGoogleDriveConfig;
import com.wcc.platform.repository.postgres.DefaultDatabaseSetup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@Import(TestGoogleDriveConfig.class)
class PlatformApplicationTests extends DefaultDatabaseSetup {

  @Autowired private ApplicationContext context;

  @Test
  void contextLoads() {
    assertNotNull(context, "The application context should have loaded");
  }
}
