package com.wcc.platform.service;

import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import java.util.List;
import org.springframework.mock.web.MockMultipartFile;

/** Utility class for Google Drive integration tests. */
public final class GoogleDriveTestUtils {

  private GoogleDriveTestUtils() {
    // Utility class
  }

  public static MockMultipartFile createMockMultipartFile(
      final String fileName, final String contentType, final byte[] content) {
    return new MockMultipartFile("file", fileName, contentType, content);
  }

  /**
   * Creates a mock file representation for testing purposes.
   *
   * @param fileId the unique identifier for the mock file
   * @param name the name of the mock file
   * @param webViewLink the web view link associated with the mock file
   * @return a mock {@link File} object with the specified values
   */
  public static File createMockFile(
      final String fileId, final String name, final String webViewLink) {
    File file = new File();
    file.setId(fileId);
    file.setName(name);
    file.setWebViewLink(webViewLink);
    return file;
  }

  /**
   * Creates a mock {@link FileList} representation for testing purposes.
   *
   * @param files the list of {@link File} objects to include in the mock {@link FileList}
   * @param nextPageToken the token indicating the next page of results
   * @return a mock {@link FileList} object containing the specified files and next page token
   */
  public static FileList createMockFileList(final List<File> files, final String nextPageToken) {
    FileList fileList = new FileList();
    fileList.setFiles(files);
    fileList.setNextPageToken(nextPageToken);
    return fileList;
  }

  /**
   * Creates a test file content as a byte array, where each byte is assigned a value cycling from 0
   * to 255.
   *
   * @param sizeInBytes the size of the resulting byte array, representing the number of bytes
   * @return a byte array containing test data of the specified size
   */
  public static byte[] createTestFileContent(final int sizeInBytes) {
    byte[] content = new byte[sizeInBytes];
    for (int i = 0; i < sizeInBytes; i++) {
      content[i] = (byte) (i % 256);
    }
    return content;
  }
}
