package com.wcc.platform.domain.platform.filestorage;

import java.util.List;

/**
 * Representation of a page of files stored in the storage service.
 *
 * @param items List of files in the current page.
 * @param nextPageToken Token to retrieve the next page, or null if there are no more pages.
 */
public record FileStoragePage(List<FileStorageItem> items, String nextPageToken) {}
