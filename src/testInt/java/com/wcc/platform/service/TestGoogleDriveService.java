package com.wcc.platform.service;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Test implementation of GoogleDriveService for integration testing.
 * This implementation stores files in memory instead of connecting to Google Drive.
 */
@Service
@Primary
@Profile("test")
public class TestGoogleDriveService extends GoogleDriveService {

  private final Map<String, File> files = new HashMap<>();
  private final String folderIdRoot;

  /** Constructor for the test service. */
  @Autowired
  public TestGoogleDriveService(Drive drive) {
    super(drive, "test-folder-id");
    this.folderIdRoot = "test-folder-id";
  }

  /**
   * Uploads a file to the in-memory storage.
   *
   * @param fileName Name of the file
   * @param contentType MIME type of the file
   * @param fileData File data as byte array
   * @return Google Drive file information
   */
  public File uploadFile(final String fileName, final String contentType, final byte[] fileData) {
    String fileId = UUID.randomUUID().toString();
    File file = new File();
    file.setId(fileId);
    file.setName(fileName);
    file.setWebViewLink("https://mock-drive.google.com/file/d/" + fileId + "/view");

    files.put(fileId, file);

    return file;
  }

  /** Uploads a file to the in-memory storage. */
  public File uploadFile(final MultipartFile file) {
    try {
      return uploadFile(file.getOriginalFilename(), file.getContentType(), file.getBytes());
    } catch (Exception e) {
      throw new PlatformInternalException("Failed to read file data", e);
    }
  }

  /** Deletes a file from the in-memory storage. */
  public void deleteFile(final String fileId) {
    if (!files.containsKey(fileId)) {
      throw new PlatformInternalException("Failed to delete file from Google Drive", null);
    }

    files.remove(fileId);
  }

  /** Gets a file from the in-memory storage. */
  public File getFile(final String fileId) {
    if (!files.containsKey(fileId)) {
      throw new PlatformInternalException("Failed to get file from Google Drive", null);
    }

    return files.get(fileId);
  }

  /** Lists files in the in-memory storage. */
  public FileList listFiles(final int pageSize) {
    FileList fileList = new FileList();
    List<File> fileItems = new ArrayList<>(files.values());
    fileList.setFiles(fileItems);

    return fileList;
  }
}
