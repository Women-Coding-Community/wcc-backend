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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SurrealDbPageRepositoryTest {

  private static final String TABLE = SurrealDbPageRepository.TABLE;
  private static final String PAGE = "{ 'page': { 'title': 'title 1' } }";

  @Mock private SyncSurrealDriver mockDriver;

  private SurrealDbPageRepository repository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    repository = new SurrealDbPageRepository(mockDriver);
  }

  @Test
  void testSave() {
    when(mockDriver.create(TABLE, PAGE)).thenReturn(PAGE);

    var savedEntity = repository.save(PAGE);

    verify(mockDriver, times(1)).create(TABLE, PAGE);
    assertEquals(PAGE, savedEntity);
  }

  @Test
  void testFindAll() {
    List<String> mockResult = Collections.singletonList(PAGE);
    when(mockDriver.select(TABLE, String.class)).thenReturn(mockResult);

    Collection<String> result = repository.findAll();

    verify(mockDriver, times(1)).select(TABLE, String.class);
    assertEquals(mockResult, result);
  }

  @Test
  void testFindByIdNotFoundCase1() {
    var result = repository.findById(PageType.FOOTER.name());

    verify(mockDriver, times(1)).query(anyString(), anyMap(), eq(String.class));
    assertTrue(result.isEmpty());
  }

  @Test
  void testFindByIdNotFoundCase2() {
    when(mockDriver.query(anyString(), anyMap(), eq(String.class))).thenReturn(List.of());

    var result = repository.findById("test-id-2");

    verify(mockDriver, times(1)).query(anyString(), anyMap(), eq(String.class));
    assertTrue(result.isEmpty());
  }

  @Test
  void testDeleteById() {
    String pageId = "test-pageId";

    repository.deleteById(pageId);

    verify(mockDriver, times(1)).delete("test-pageId");
  }
}
