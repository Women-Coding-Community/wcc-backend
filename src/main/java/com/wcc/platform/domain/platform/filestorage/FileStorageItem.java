package com.wcc.platform.domain.platform.filestorage;

/**
 * Representation of a single file item in the storage service.
 *
 * @param id Unique identifier of the file.
 * @param name Name of the file.
 * @param webViewLink Link to view the file in a browser.
 */
public record FileStorageItem(String id, String name, String webViewLink) {}
