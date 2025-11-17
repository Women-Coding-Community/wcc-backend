package com.wcc.platform.bootstrap;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for Argon2 hashing algorithm. This class binds to properties under the
 * prefix "security.argon2" in the application configuration file and provides customization options
 * for the Argon2 password hashing algorithm.
 *
 * <p>Properties: - saltLength: The length of the salt in bytes. Default is 16. - hashLength: The
 * length of the resulting hash in bytes. Default is 32. - parallelism: The number of parallel
 * threads used for hashing. Default is 1. - memory: The memory cost parameter, representing the
 * amount of memory (in kibibytes) used during hashing. Default is 65536 (64 MiB). - iterations: The
 * number of iterations used during hashing. Default is 3.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.seed.admin")
public class DevAdminSeederProperties {
  private boolean enabled;
  private String email;
  private String password;
}
