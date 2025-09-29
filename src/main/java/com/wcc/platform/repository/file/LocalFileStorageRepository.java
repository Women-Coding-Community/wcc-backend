package com.wcc.platform.repository.file;

import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.domain.platform.filestorage.FileStored;
import com.wcc.platform.properties.FolderStorageProperties;
import com.wcc.platform.repository.FileStorageRepository;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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

  private static Path uniquePath(final Path desired) {
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
    final Path parent = desired.getParent();
    int counter = 1;
    while (true) {
      final var candidate = parent.resolve(name + " (" + counter + ")" + ext);
      if (!Files.exists(candidate)) {
        return candidate;
      }
      counter++;
    }
  }

  /**
   * For local storage, expose as file:// URL or simple encoded path; adjust if app serves static
   * files.
   */
  private static String toWebLink(final Path path) {
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

      String safeName = StringUtils.defaultIfBlank(fileName, "file");
      safeName = Paths.get(safeName).getFileName().toString();
      if (StringUtils.isBlank(safeName)) {
        safeName = "file";
      }

      final Path target = uniquePath(folderPath.resolve(safeName)).normalize();

      if (!target.startsWith(folderPath)) {
        throw new PlatformInternalException("Invalid target path", null);
      }

      Files.write(target, fileData, StandardOpenOption.CREATE_NEW);
      final String id = target.toAbsolutePath().toString();
      final String link = toWebLink(target);
      log.info("Stored local file '{}' ({} bytes) at {}", safeName, fileData.length, target);
      return new FileStored(id, link);
    } catch (IOException e) {
      log.error("Failed to store local file", e);
      throw new PlatformInternalException("Failed to store local file", e);
    }
  }

  @Override
  public FileStored uploadFile(final MultipartFile file, final String folderId) {
    try {
      return uploadFile(
          file.getOriginalFilename(), file.getContentType(), file.getBytes(), folderId);
    } catch (IOException e) {
      throw new PlatformInternalException("Failed to read multipart file", e);
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
    } catch (IOException e) {
      log.warn("Could not delete local file {}: {}", fileId, e.getMessage());
    }
  }

  private Path resolveFolder(final String folder) {
    final Path base = Path.of(baseDirectory).normalize();

    final Set<String> allowed =
        Stream.of(
                folders.getMainFolder(),
                folders.getResourcesFolder(),
                folders.getMentorsFolder(),
                folders.getMentorsProfileFolder(),
                folders.getEventsFolder(),
                folders.getImagesFolder())
            .filter(Objects::nonNull)
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.toSet());

    if (StringUtils.isBlank(folder)) {
      return base;
    }

    if (!allowed.contains(folder)) {
      throw new PlatformInternalException("Folder is not allowed: " + folder, null);
    }

    final Path resolved = base.resolve(folder).normalize();
    if (!resolved.startsWith(base)) {
      throw new PlatformInternalException("Resolved folder escapes base directory", null);
    }
    return resolved;
  }
}
