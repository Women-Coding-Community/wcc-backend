package com.wcc.platform.repository.local;

import static org.junit.jupiter.api.Assertions.*;

import com.wcc.platform.domain.platform.filestorage.FileStored;
import com.wcc.platform.properties.FolderStorageProperties;
import com.wcc.platform.repository.file.LocalFileStorageRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class LocalFileStorageRepositoryTest {

  private Path tempDir;
  private FolderStorageProperties folders;
  private LocalFileStorageRepository repo;

  private static void injectBaseDirectory(LocalFileStorageRepository r, String baseDir) {
    try {
      var f = LocalFileStorageRepository.class.getDeclaredField("baseDirectory");
      f.setAccessible(true);
      f.set(r, baseDir);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @BeforeEach
  void setUp() throws IOException {
    tempDir = Files.createTempDirectory("wcc-local-test");
    folders = new FolderStorageProperties();
    // map some "folder ids" to subfolders
    folders.setMainFolder("main");
    folders.setResourcesFolder("resources");
    folders.setMentorsFolder("mentors");
    folders.setMentorsProfileFolder("mentors-profile");
    folders.setEventsFolder("events");
    folders.setImagesFolder("images");
    repo = new LocalFileStorageRepository(folders);
    // inject baseDirectory property (normally comes from @Value)
    injectBaseDirectory(repo, tempDir.toString());
  }

  @AfterEach
  void tearDown() throws IOException {
    if (tempDir != null) {
      // recursive delete
      Files.walk(tempDir)
          .sorted((a, b) -> b.compareTo(a))
          .forEach(
              p -> {
                try {
                  Files.deleteIfExists(p);
                } catch (IOException ignored) {
                }
              });
    }
  }

  @Test
  void uploadFile_writesBytes_andReturnsFileStored_withFileLink() throws IOException {
    byte[] data = "hello".getBytes();
    FileStored stored = repo.uploadFile("greeting.txt", "text/plain", data, folders.getImagesFolder());

    assertNotNull(stored);
    assertTrue(stored.id().startsWith(tempDir.toAbsolutePath().toString()));
    assertTrue(stored.webLink().startsWith("file://"));

    Path written = Path.of(stored.id());
    assertTrue(Files.exists(written));
    assertEquals("hello", Files.readString(written));
  }

  @Test
  void uploadFile_usesDefaultName_whenBlank_andAvoidsOverwriteWithUniqueName() throws IOException {
    // First file with blank name -> defaults to "file"
    byte[] data1 = "one".getBytes();
    FileStored s1 = repo.uploadFile(StringUtils.EMPTY, "text/plain", data1, folders.getResourcesFolder());

    // Write another with blank name into same folder -> should create "file (1)"
    byte[] data2 = "two".getBytes();
    FileStored s2 = repo.uploadFile("  ", "text/plain", data2, folders.getResourcesFolder());

    assertNotEquals(s1.id(), s2.id());
    assertTrue(Files.exists(Path.of(s1.id())));
    assertTrue(Files.exists(Path.of(s2.id())));
    assertEquals("one", Files.readString(Path.of(s1.id())));
    assertEquals("two", Files.readString(Path.of(s2.id())));
  }

  @Test
  void uploadMultipartFile_delegatesAndWrites() throws IOException {
    var mf = new MockMultipartFile("file", "demo.bin", "application/octet-stream", new byte[] {1,2,3});
    FileStored stored = repo.uploadFile(mf, folders.getMainFolder());
    assertNotNull(stored);
    assertTrue(Files.exists(Path.of(stored.id())));
    assertEquals(3, Files.readAllBytes(Path.of(stored.id())).length);
  }

  @Test
  void deleteFile_removesExisting_andNoOpForBlank() throws IOException {
    // Create a file
    FileStored s = repo.uploadFile("del.txt", "text/plain", "x".getBytes(), folders.getEventsFolder());
    Path p = Path.of(s.id());
    assertTrue(Files.exists(p));

    // Delete
    repo.deleteFile(s.id());
    assertFalse(Files.exists(p));

    // No exception for blank
    assertDoesNotThrow(() -> repo.deleteFile(" "));
  }

  @Test
  void getFolders_returnsInjectedProperties() {
    assertSame(folders, repo.getFolders());
  }
}
