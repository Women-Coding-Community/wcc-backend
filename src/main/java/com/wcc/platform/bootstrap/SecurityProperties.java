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
@ConfigurationProperties(prefix = "security.argon2")
public class SecurityProperties {

  /**
   * Default salt length used in Argon2 hashing, measured in bytes. A 16-byte (128-bit) salt
   * provides enough randomness to ensure that each hashed password is unique, even if two users
   * have the same password.
   */
  private static final int DEFAULT_SALT_LENGTH = 16;

  /** hash length (in bytes). */
  private static final int DEFAULT_HASH_LENGTH = 32;

  /** parallelism (threads). */
  private static final int DEFAULT_PARALLELISM = 1;

  /** memory cost (in KB) = 64MB. */
  private static final int DEFAULT_MEMORY = 65_536;

  private static final int DEFAULT_ITERATIONS = 3;

  private int saltLength = DEFAULT_SALT_LENGTH;
  private int hashLength = DEFAULT_HASH_LENGTH;
  private int parallelism = DEFAULT_PARALLELISM;
  private int memory = DEFAULT_MEMORY;
  private int iterations = DEFAULT_ITERATIONS;
}
