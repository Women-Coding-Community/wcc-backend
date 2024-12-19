package com.wcc.platform.repository.surrealdb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.surrealdb.driver.SyncSurrealDriver;
import com.wcc.platform.domain.cms.PageType;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SurrealDbPageRepositoryTest {

  private static final String TABLE = SurrealDbPageRepository.TABLE;

  @Mock private SyncSurrealDriver mockDriver;

  private SurrealDbPageRepository repository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    repository = new SurrealDbPageRepository(mockDriver);
  }

  @Test
  @SuppressWarnings("unchecked")
  void testCreate() {
    var page = Map.of("page", Map.of("title", "title 1"));
    when(mockDriver.create(TABLE, page)).thenReturn(page);

    var savedEntity = repository.create((Map<String, Object>) (Map) page);

    verify(mockDriver, times(1)).create(TABLE, page);
    assertEquals(page, savedEntity);
  }

  @Test
  @SuppressWarnings("unchecked")
  void testUpdate() {
    var id = "test-id";
    var page = Map.of("page", Map.of("title", "title 2"));
    when(mockDriver.update(id, page)).thenReturn(List.of(page));

    var savedEntity = repository.update(id, (Map<String, Object>) (Map) page);

    verify(mockDriver, times(1)).update(id, page);
    assertEquals(page, savedEntity);
  }

  @Test
  void testFindByIdNotFoundCase1() {
    var result = repository.findById(PageType.FOOTER.name());

    verify(mockDriver, times(1))
        .query("SELECT * FROM page WHERE id = $id", Map.of("id", "FOOTER"), Map.class);
    assertTrue(result.isEmpty());
  }

  @Test
  void testFindByIdNotFoundCase2() {
    when(mockDriver.query(anyString(), anyMap(), eq(String.class))).thenReturn(List.of());

    var result = repository.findById("test-id-2");

    verify(mockDriver, times(1))
        .query("SELECT * FROM page WHERE id = $id", Map.of("id", "test-id-2"), Map.class);
    assertTrue(result.isEmpty());
  }

  @Test
  void testDeleteById() {
    String pageId = "test-pageId";

    repository.deleteById(pageId);

    verify(mockDriver, times(1)).delete("test-pageId");
  }
}
