package com.wcc.platform.repository;

import com.wcc.platform.domain.platform.filestorage.FileStored;
import com.wcc.platform.properties.FolderStorageProperties;
import com.wcc.platform.repository.googledrive.GoogleDriveRepository;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interface to upload files to a file storage service.
 *
 * <p>Currently implemented by {@link GoogleDriveRepository}.
 */
public interface FileStorageRepository {

  FolderStorageProperties getFolders();

  /**
   * Uploads a file to File Storage Service.
   *
   * @param fileName Name of the file
   * @param contentType MIME type of the file
   * @param fileData File data as byte-array
   * @param folder Can be the folder name or folder ID
   * @return file information
   */
  FileStored uploadFile(String fileName, String contentType, byte[] fileData, String folder);

  /**
   * Uploads a file to a file storage service into a specified folder.
   *
   * @param file the file to be uploaded, represented as a {@code MultipartFile}
   * @param folderId the identifier of the folder where the file should be uploaded
   * @return a {@code FileStored} object containing information about the uploaded file, such as its
   *     ID and web link
   */
  FileStored uploadFile(MultipartFile file, String folderId);

  /**
   * Deletes a file from the file storage service.
   *
   * @param fileId the unique identifier of the file to be deleted
   */
  void deleteFile(String fileId);
}
