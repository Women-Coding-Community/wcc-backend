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
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SurrealDbPageRepositoryTest {

  private static final String TABLE = SurrealDbPageRepository.TABLE;

  @Mock private SyncSurrealDriver mockDriver;

  @InjectMocks private SurrealDbPageRepository repository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testSave() {
    Object entity = new Object();
    when(mockDriver.create(TABLE, entity)).thenReturn(entity);

    var savedEntity = repository.save(entity);

    verify(mockDriver, times(1)).create(TABLE, entity);
    assertEquals(entity, savedEntity);
  }

  @Test
  void testFindAll() {
    List<Object> mockResult = List.of(new Object(), new Object());

    when(mockDriver.select(TABLE, Object.class)).thenReturn(mockResult);

    Collection<Object> result = repository.findAll();

    verify(mockDriver, times(1)).select(TABLE, Object.class);
    assertEquals(mockResult, result);
  }

  @Test
  void testFindByIdNotFoundCase1() {
    Optional<Object> result = repository.findById("test-id");

    verify(mockDriver, times(1)).query(anyString(), anyMap(), eq(Object.class));
    assertTrue(result.isEmpty());
  }

  @Test
  void testFindByIdNotFoundCase2() {
    when(mockDriver.query(anyString(), anyMap(), eq(Object.class))).thenReturn(List.of());

    Optional<Object> result = repository.findById("test-id-2");

    verify(mockDriver, times(1)).query(anyString(), anyMap(), eq(Object.class));
    assertTrue(result.isEmpty());
  }

  @Test
  void testDeleteById() {
    String pageId = "test-pageId";

    repository.deleteById(pageId);

    verify(mockDriver, times(1)).delete("test-pageId");
  }
}
