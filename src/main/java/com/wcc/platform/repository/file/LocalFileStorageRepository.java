package com.wcc.platform.repository.file;

import com.wcc.platform.domain.platform.filestorage.FileStored;
import com.wcc.platform.properties.FolderStorageProperties;
import com.wcc.platform.repository.FileStorageRepository;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/** Local filesystem implementation of FileStorageRepository. */
@Slf4j
@Service
@ConditionalOnProperty(prefix = "storage", name = "type", havingValue = "local")
@RequiredArgsConstructor
public class LocalFileStorageRepository implements FileStorageRepository {

  private static final String FILE_PATH = "file://";
  private final FolderStorageProperties folders;

  /** Base directory where files are stored locally. */
  @Value("${file.storage.directory}")
  private String baseDirectory;

  private static Path uniquePath(Path desired) throws IOException {
    if (!Files.exists(desired)) {
      return desired;
    }
    final String fileName = desired.getFileName().toString();
    final String name;
    final String ext;
    final int dot = fileName.lastIndexOf('.');
    if (dot > 0) {
      name = fileName.substring(0, dot);
      ext = fileName.substring(dot);
    } else {
      name = fileName;
      ext = "";
    }
    Path parent = desired.getParent();
    int i = 1;
    while (true) {
      Path candidate = parent.resolve(name + " (" + i + ")" + ext);
      if (!Files.exists(candidate)) {
        return candidate;
      }
      i++;
    }
  }

  /**
   * For local storage, expose as file:// URL or simple encoded path; adjust if app serves static
   * files.
   */
  private static String toWebLink(Path path) {
    return FILE_PATH + URLEncoder.encode(path.toAbsolutePath().toString(), StandardCharsets.UTF_8);
  }

  @Override
  public FolderStorageProperties getFolders() {
    return folders;
  }

  @Override
  public FileStored uploadFile(
      final String fileName, final String contentType, final byte[] fileData, final String folder) {
    try {
      final Path folderPath = resolveFolder(folder);
      Files.createDirectories(folderPath);
      final String safeName = StringUtils.defaultIfBlank(fileName, "file");
      final Path target = uniquePath(folderPath.resolve(safeName));
      Files.write(target, fileData, StandardOpenOption.CREATE_NEW);
      final String id = target.toAbsolutePath().toString();
      final String link = toWebLink(target);
      log.info("Stored local file '{}' ({} bytes) at {}", safeName, fileData.length, target);
      return new FileStored(id, link);
    } catch (IOException e) {
      throw new RuntimeException("Failed to store local file", e);
    }
  }

  @Override
  public FileStored uploadFile(final MultipartFile file, final String folderId) {
    try {
      return uploadFile(
          file.getOriginalFilename(), file.getContentType(), file.getBytes(), folderId);
    } catch (IOException e) {
      throw new RuntimeException("Failed to read multipart file", e);
    }
  }

  @Override
  public void deleteFile(final String fileId) {
    if (StringUtils.isBlank(fileId)) {
      return;
    }
    try {
      final Path path = Path.of(fileId);
      Files.deleteIfExists(path);
      log.info("Deleted local file {}", path);
    } catch (Exception e) {
      log.warn("Could not delete local file {}: {}", fileId, e.getMessage());
    }
  }

  private Path resolveFolder(final String folder) {
    final Path base = Path.of(baseDirectory);
    if (StringUtils.isBlank(folder)) {
      return base;
    }
    return base.resolve(folder);
  }
}
