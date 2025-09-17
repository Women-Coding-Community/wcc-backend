package com.wcc.platform.repository.local;

import static org.assertj.core.api.Assertions.assertThat;

import com.wcc.platform.properties.FolderStorageProperties;
import com.wcc.platform.repository.file.LocalFileStorageRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(
    properties = {"storage.type=local", "file.storage.directory=${user.dir}/data-test-int"})
@DisplayName("LocalFileStorageRepository Integration Tests")
class LocalFileStorageRepositoryIntegrationTest {

  @Autowired private LocalFileStorageRepository repository;
  @Autowired private FolderStorageProperties folders;

  private Path baseDir;

  @BeforeEach
  void setUp() {
    baseDir = Path.of(System.getProperty("user.dir"), "data-test-int");
  }

  @AfterEach
  void tearDown() throws IOException {
    if (baseDir != null && Files.exists(baseDir)) {
      cleanupFiles();
    }
  }

  private void cleanupFiles() throws IOException {
    Files.walk(baseDir)
        .sorted((a, b) -> b.compareTo(a))
        .forEach(
            p -> {
              try {
                Files.deleteIfExists(p);
              } catch (IOException ignored) {
                // ignore
              }
            });
  }

  @Test
  @DisplayName("Should upload and delete files on local filesystem")
  void shouldUploadAndDelete() throws Exception {
    // Ensure folders mapping exists (Spring binds defaults; we can set if nulls)
    if (folders.getImagesFolder() == null) {
      folders.setImagesFolder("images");
    }

    byte[] content = "integration hello".getBytes();

    var stored =
        repository.uploadFile("int.txt", "text/plain", content, folders.getResourcesFolder());

    assertThat(stored).isNotNull();
    assertThat(stored.id()).isNotBlank();
    assertThat(stored.webLink()).startsWith("file://");

    Path written = Path.of(stored.id());
    assertThat(Files.exists(written)).isTrue();
    assertThat(Files.readString(written)).isEqualTo("integration hello");

    var file =
        new MockMultipartFile(
            "file", "second.bin", "application/octet-stream", new byte[] {1, 2, 3});
    var stored2 = repository.uploadFile(file, folders.getResourcesFolder());
    assertThat(stored2).isNotNull();
    assertThat(Files.exists(Path.of(stored2.id()))).isTrue();

    repository.deleteFile(stored.id());
    assertThat(Files.exists(written)).isFalse();
  }
}
