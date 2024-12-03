package com.wcc.platform.utils;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/** Util class for pagination data for any page. */
@Slf4j
public final class PaginationUtil {

  private PaginationUtil() {}

  /**
   * Get the items for the currentPage with specific pageSize.
   *
   * @param items list od data items
   * @param currentPage page number requested
   * @param pageSize items per page
   * @return list of items on the current page
   */
  public static <T> List<T> getPaginatedResult(
      @NotEmpty(message = "Items list cannot be null or empty.") final List<T> items,
      final int currentPage,
      @Min(value = 1, message = "Page size must be greater than zero") final int pageSize) {
    final int totalItems = items.size();
    final int totalPages = getTotalPages(items, pageSize);

    if (currentPage < 1 || currentPage > totalPages) {
      throw new IllegalArgumentException(
          "currentPage exceeds total pages. Total Pages: "
              + totalPages
              + ", Current Page: "
              + currentPage);
    }

    final int fromIndex = (currentPage - 1) * pageSize;
    final int toIndex = Math.min(fromIndex + pageSize, totalItems); // Handle the last page
    return items.subList(fromIndex, toIndex);
  }

  /**
   * Get the total pages for the requested data items
   *
   * @param items data items
   * @param pageSize items per page
   * @return no. of pages
   */
  public static <T> int getTotalPages(final List<T> items, final int pageSize) {
    final int totalItems = items.size();
    return (int) Math.ceil((double) totalItems / pageSize);
  }
}
