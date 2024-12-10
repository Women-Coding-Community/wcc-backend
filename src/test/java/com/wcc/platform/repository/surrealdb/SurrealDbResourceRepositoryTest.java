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
import com.wcc.platform.domain.platform.ResourceContent;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SurrealDbResourceRepositoryTest {

  private static final String TEST_ID = "test-id";
  private static final String TABLE = SurrealDbResourceRepository.TABLE;

  private final ResourceContent entity = new ResourceContent();
  private final Class<ResourceContent> className = ResourceContent.class;

  @Mock private SyncSurrealDriver mockDriver;

  @InjectMocks private SurrealDbResourceRepository repository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testCreate() {
    when(mockDriver.create(TABLE, entity)).thenReturn(entity);

    var savedEntity = repository.create(entity);

    verify(mockDriver, times(1)).create(TABLE, entity);
    assertEquals(entity, savedEntity);
  }

  @Test
  void testFindAll() {
    List<ResourceContent> mockResult = List.of(entity);

    when(mockDriver.select(TABLE, className)).thenReturn(mockResult);

    var result = repository.findAll();

    verify(mockDriver, times(1)).select(TABLE, className);
    assertEquals(mockResult, result);
  }

  @Test
  void testFindByIdNotFoundCase1() {
    var result = repository.findById(TEST_ID);

    verify(mockDriver, times(1)).query(anyString(), anyMap(), eq(className));
    assertTrue(result.isEmpty());
  }

  @Test
  void testFindByIdNotFoundCase2() {
    when(mockDriver.query(anyString(), anyMap(), eq(className))).thenReturn(List.of());

    var result = repository.findById(TEST_ID);

    verify(mockDriver, times(1)).query(anyString(), anyMap(), eq(className));
    assertTrue(result.isEmpty());
  }

  @Test
  void testDeleteById() {
    repository.deleteById(TEST_ID);

    verify(mockDriver, times(1)).delete(TEST_ID);
  }
}
