package com.wcc.platform.service;

import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import java.util.List;
import org.springframework.mock.web.MockMultipartFile;

public final class GoogleDriveTestUtils {

  private GoogleDriveTestUtils() {
    // Utility class
  }

  public static MockMultipartFile createMockMultipartFile(
      String fileName, String contentType, byte[] content) {
    return new MockMultipartFile("file", fileName, contentType, content);
  }

  public static File createMockFile(String id, String name, String webViewLink) {
    File file = new File();
    file.setId(id);
    file.setName(name);
    file.setWebViewLink(webViewLink);
    return file;
  }

  public static FileList createMockFileList(List<File> files, String nextPageToken) {
    FileList fileList = new FileList();
    fileList.setFiles(files);
    fileList.setNextPageToken(nextPageToken);
    return fileList;
  }

  public static byte[] createTestFileContent(int sizeInBytes) {
    byte[] content = new byte[sizeInBytes];
    for (int i = 0; i < sizeInBytes; i++) {
      content[i] = (byte) (i % 256);
    }
    return content;
  }
}
