package com.wcc.platform.utils;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

/** Util class for pagination data for any page. */
@Slf4j
public class PaginationUtil {

  private PaginationUtil() {}

  /**
   * Get the items for the currentPage with specific pageSize.
   *
   * @param items list od data items
   * @param currentPage page number requested
   * @param pageSize items per page
   * @return list of items on the current page
   */
  public static <T> List<T> getPaginatedResult(List<T> items, int currentPage, int pageSize) {
    if (items == null || items.isEmpty()) {
      throw new IllegalArgumentException("Items list cannot be null or empty.");
    }
    if (pageSize <= 0) {
      throw new IllegalArgumentException("Page size must be greater than zero.");
    }
    int totalItems = items.size();
    int totalPages = getTotalPages(items, pageSize);

    if (currentPage < 1 || currentPage > totalPages) {
      throw new IllegalArgumentException(
          "currentPage exceeds total pages. Total Pages: "
              + totalPages
              + ", Current Page: "
              + currentPage);
    }

    int fromIndex = (currentPage - 1) * pageSize;
    int toIndex = Math.min(fromIndex + pageSize, totalItems); // Handle the last page
    return items.subList(fromIndex, toIndex);
  }

  /**
   * Get the total pages for the requested data items
   *
   * @param items data items
   * @param pageSize items per page
   * @return no. of pages
   */
  public static <T> int getTotalPages(List<T> items, int pageSize) {
    if (items == null || items.isEmpty()) {
      throw new IllegalArgumentException("Items list cannot be null or empty.");
    }
    if (pageSize <= 0) {
      throw new IllegalArgumentException("Page size must be greater than zero.");
    }
    int totalItems = items.size();
    return (int) Math.ceil((double) totalItems / pageSize);
  }
}
