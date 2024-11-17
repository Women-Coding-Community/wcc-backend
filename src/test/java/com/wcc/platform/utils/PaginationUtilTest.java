package com.wcc.platform.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PaginationUtilTest {

  List<String> items;

  @BeforeEach
  void setup() {
    items = List.of("item1", "item2", "item3", "item4", "item5");
  }

  @Test
  void testForTotalPages() {
    var totalPages = PaginationUtil.getTotalPages(items, 2);
    assertEquals(3, totalPages);
  }

  @Test
  void testIncorrectPageSize() {
    var exception =
        assertThrows(IllegalArgumentException.class, () -> PaginationUtil.getTotalPages(items, 0));
    assertEquals("Page size must be greater than zero.", exception.getMessage());
  }

  @Test
  void testEmptyItemsForTotalPages() {
    var exception =
        assertThrows(IllegalArgumentException.class, () -> PaginationUtil.getTotalPages(null, 1));
    assertEquals("Items list cannot be null or empty.", exception.getMessage());
  }

  @Test
  void testEmptyItems() {
    var exception =
        assertThrows(
            IllegalArgumentException.class, () -> PaginationUtil.getPaginatedResult(null, 1, 1));
    assertEquals("Items list cannot be null or empty.", exception.getMessage());
  }

  @Test
  void testIncorrectPageSizeForPaginatedResult() {
    var exception =
        assertThrows(
            IllegalArgumentException.class, () -> PaginationUtil.getPaginatedResult(items, 1, 0));
    assertEquals("Page size must be greater than zero.", exception.getMessage());
  }

  @Test
  void testPaginatedResult() {
    var paginatedResultPage1 = PaginationUtil.getPaginatedResult(items, 1, 2);
    var expectedResultPage1 = List.of("item1", "item2");

    assertEquals(expectedResultPage1, paginatedResultPage1);

    var paginatedResultPage2 = PaginationUtil.getPaginatedResult(items, 2, 2);
    var expectedResultPage2 = List.of("item3", "item4");

    assertEquals(expectedResultPage2, paginatedResultPage2);

    var paginatedResultPage3 = PaginationUtil.getPaginatedResult(items, 3, 2);
    var expectedResultPage3 = List.of("item5");

    assertEquals(expectedResultPage3, paginatedResultPage3);
  }

  @Test
  void testIncorrectCurrentPage() {
    var exception =
        assertThrows(
            IllegalArgumentException.class, () -> PaginationUtil.getPaginatedResult(items, 4, 2));
    assertEquals(
        "currentPage exceeds total pages. Total Pages: 3, Current Page: 4", exception.getMessage());
  }
}
