package com.wcc.platform.configuration;

import com.wcc.platform.bootstrap.SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;

/**
 * TestConfig is a Spring test configuration class. It is annotated with {@code @TestConfiguration},
 * indicating it is used for configuration in the context of testing.
 *
 * <p>This configuration class enables the binding of application properties to the {@code
 * SecurityProperties} class by using the {@code @EnableConfigurationProperties} annotation,
 * facilitating easy management and injection of Argon2-related security configuration properties
 * during tests.
 *
 * <p>Used specifically in testing scenarios to provide the required beans and configurations
 * related to security properties.
 */
@TestConfiguration
@EnableConfigurationProperties(SecurityProperties.class)
public class TestConfig {}
